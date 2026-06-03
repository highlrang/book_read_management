package com.liber.book_read_management.service

import com.liber.book_read_management.client.NaverSearchClient
import com.liber.book_read_management.dto.aladin.AladinBookSearchResponse
import com.liber.book_read_management.dto.naver.NaverBookSearchResponse
import com.liber.book_read_management.entities.NaverBookImage
import com.liber.book_read_management.repository.NaverBookImageRepository
import feign.FeignException
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.cache.CacheManager
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

private val logger = KotlinLogging.logger {}
private const val MISS_CACHE_VALUE = "__MISS__"

@Service
class NaverBookImageService(
    private val naverSearchClient: NaverSearchClient,
    private val cacheManager: CacheManager,
    private val naverBookImageRepository: NaverBookImageRepository,
    @Value("\${client.naver.search.enabled:true}")
    private val enabled: Boolean,
    @Value("\${client.naver.search.min-interval-ms:120}")
    private val minIntervalMillis: Long,
    @Value("\${client.naver.search.cooldown-on-too-many-requests-ms:60000}")
    private val cooldownOnTooManyRequestsMillis: Long,
    @Value("\${client.naver.search.max-list-external-lookups:5}")
    private val maxListExternalLookups: Int
) {
    private val nextAllowedRequestAt = AtomicLong(0)

    fun resolveListCovers(items: List<AladinBookSearchResponse.AladinItem>): List<String?> {
        val candidatesByItem = items.map { item -> extractCandidateIsbns(item.isbn13, item.isbn) }
        val storedImagesByIsbn = naverBookImageRepository.findAllByIsbnIn(candidatesByItem.flatten().distinct())
            .associateBy { it.isbn }
        val externalLookupBudget = AtomicInteger(maxListExternalLookups.coerceAtLeast(0))
        return candidatesByItem.map { candidates ->
            resolveCover(candidates, storedImagesByIsbn, externalLookupBudget)
        }
    }

    fun resolveCover(isbn13: String?, isbn: String?): String? {
        return resolveCover(extractCandidateIsbns(isbn13, isbn), emptyMap(), null)
    }

    private fun resolveCover(
        candidates: List<String>,
        storedImagesByIsbn: Map<String, NaverBookImage>,
        externalLookupBudget: AtomicInteger?
    ): String? {
        for (candidate in candidates) {
            val storedImage = storedImagesByIsbn[candidate]
            if (storedImage != null) {
                if (storedImage.imageUrl != null) {
                    return storedImage.imageUrl
                }
                continue
            }

            val imageUrl = searchBookImageByIsbn(candidate, externalLookupBudget)
            if (imageUrl != null) {
                return imageUrl
            }
        }

        return null
    }

    fun searchBookImageByIsbn(isbn: String): String? {
        return searchBookImageByIsbn(isbn, null)
    }

    private fun searchBookImageByIsbn(
        isbn: String,
        externalLookupBudget: AtomicInteger?
    ): String? {
        val cache = cacheManager.getCache("naverBookImages")
        cache?.get(isbn, String::class.java)?.let { cachedValue ->
            return cachedValue.takeUnless { it == MISS_CACHE_VALUE }
        }

        naverBookImageRepository.findByIsbn(isbn)?.let { storedImage ->
            cache?.put(isbn, storedImage.imageUrl ?: MISS_CACHE_VALUE)
            return storedImage.imageUrl
        }

        if (!enabled || !tryAcquireExternalLookup(externalLookupBudget) || !tryAcquireRateLimit()) {
            return null
        }

        val response = runCatching {
            naverSearchClient.searchBook(
                query = isbn,
                display = 1,
                start = 1,
                sort = "sim"
            )
        }.onFailure { exception ->
            if (exception is FeignException.TooManyRequests) {
                applyCooldown()
            }
            logger.warn(exception) { "Failed to search Naver book image for isbn=$isbn" }
        }.getOrNull() ?: return null

        val imageUrl = response.items
            .asSequence()
            .firstOrNull { item -> matchesIsbn(item, isbn) }
            ?.image
            ?.takeIf { it.isNotBlank() }

        cache?.put(isbn, imageUrl ?: MISS_CACHE_VALUE)
        saveImageLookupResult(isbn, imageUrl)
        return imageUrl
    }

    private fun saveImageLookupResult(isbn: String, imageUrl: String?) {
        runCatching {
            val image = naverBookImageRepository.findByIsbn(isbn)
                ?: NaverBookImage(isbn = isbn)
            image.imageUrl = imageUrl
            naverBookImageRepository.save(image)
        }.onFailure { exception ->
            if (exception !is DataIntegrityViolationException) {
                logger.warn(exception) { "Failed to save Naver book image lookup result for isbn=$isbn" }
            }
        }
    }

    private fun tryAcquireExternalLookup(externalLookupBudget: AtomicInteger?): Boolean {
        if (externalLookupBudget == null) {
            return true
        }

        while (true) {
            val remaining = externalLookupBudget.get()
            if (remaining <= 0) {
                return false
            }
            if (externalLookupBudget.compareAndSet(remaining, remaining - 1)) {
                return true
            }
        }
    }

    private fun tryAcquireRateLimit(): Boolean {
        val now = System.currentTimeMillis()

        while (true) {
            val nextAllowedAt = nextAllowedRequestAt.get()
            if (now < nextAllowedAt) {
                return false
            }

            val nextRequestAt = now + minIntervalMillis.coerceAtLeast(0)
            if (nextAllowedRequestAt.compareAndSet(nextAllowedAt, nextRequestAt)) {
                return true
            }
        }
    }

    private fun applyCooldown() {
        val cooldownMillis = cooldownOnTooManyRequestsMillis.coerceAtLeast(0)
        val cooldownUntil = System.currentTimeMillis() + cooldownMillis
        nextAllowedRequestAt.updateAndGet { current -> maxOf(current, cooldownUntil) }
    }

    private fun extractCandidateIsbns(isbn13: String?, isbn: String?): List<String> {
        return listOfNotNull(
            extractPrimaryIsbn(isbn13, 13),
            extractPrimaryIsbn(isbn, 13),
            extractPrimaryIsbn(isbn, 10)
        ).distinct()
    }

    private fun matchesIsbn(item: NaverBookSearchResponse.NaverBookItem, isbn: String): Boolean {
        val normalizedIsbns = item.isbn
            ?.split(" ")
            ?.map { value -> value.trim() }
            ?.filter { value -> value.isNotBlank() }
            ?: return false

        return normalizedIsbns.contains(isbn)
    }

    private fun extractPrimaryIsbn(rawIsbn: String?, length: Int): String? {
        return rawIsbn
            ?.split(" ")
            ?.map { value -> value.trim() }
            ?.firstOrNull { value -> value.length == length && value.all(Char::isDigit) }
    }
}
