package com.liber.book_read_management.service

import com.liber.book_read_management.client.NaverSearchClient
import com.liber.book_read_management.dto.aladin.AladinBookSearchResponse
import com.liber.book_read_management.dto.naver.NaverBookSearchResponse
import mu.KotlinLogging
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Service

private val logger = KotlinLogging.logger {}
private const val MISS_CACHE_VALUE = "__MISS__"

@Service
class NaverBookImageService(
    private val naverSearchClient: NaverSearchClient,
    private val cacheManager: CacheManager
) {

    fun resolveListCovers(items: List<AladinBookSearchResponse.AladinItem>): List<String?> {
        return items.map { item ->
            resolveCover(item.isbn13, item.isbn)
        }
    }

    fun resolveCover(isbn13: String?, isbn: String?): String? {
        val candidates = listOfNotNull(
            extractPrimaryIsbn(isbn13, 13),
            extractPrimaryIsbn(isbn, 13),
            extractPrimaryIsbn(isbn, 10)
        ).distinct()

        for (candidate in candidates) {
            val imageUrl = searchBookImageByIsbn(candidate)
            if (imageUrl != null) {
                return imageUrl
            }
        }

        return null
    }

    fun searchBookImageByIsbn(isbn: String): String? {
        val cache = cacheManager.getCache("naverBookImages")
        cache?.get(isbn, String::class.java)?.let { cachedValue ->
            return cachedValue.takeUnless { it == MISS_CACHE_VALUE }
        }

        val response = runCatching {
            naverSearchClient.searchBook(
                query = isbn,
                display = 1,
                start = 1,
                sort = "sim"
            )
        }.onFailure { exception ->
            logger.warn(exception) { "Failed to search Naver book image for isbn=$isbn" }
        }.getOrNull() ?: return null

        val imageUrl = response.items
            .asSequence()
            .firstOrNull { item -> matchesIsbn(item, isbn) }
            ?.image
            ?.takeIf { it.isNotBlank() }

        cache?.put(isbn, imageUrl ?: MISS_CACHE_VALUE)
        return imageUrl
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
