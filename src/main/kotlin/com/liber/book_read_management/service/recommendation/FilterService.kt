package com.liber.book_read_management.service.recommendation

import com.liber.book_read_management.repository.BookReadLogRepository
import com.liber.book_read_management.service.recommendation.model.FilterResult
import com.liber.book_read_management.service.recommendation.model.RecommendationCandidate
import org.springframework.stereotype.Service

@Service
class FilterService(
    private val bookReadLogRepository: BookReadLogRepository
) {

    fun filter(userId: Long, candidates: List<RecommendationCandidate>): FilterResult {
        val readIsbns = bookReadLogRepository.findAllByUserId(userId)
            .map { it.bookIsbn.trim() }
            .toSet()

        val seenIsbns = mutableSetOf<String>()
        val filtered = mutableListOf<RecommendationCandidate>()
        var removedCount = 0

        candidates.forEach { candidate ->
            when {
                !hasRequiredMetadata(candidate) -> {
                    removedCount += 1
                }
                !seenIsbns.add(normalizedIsbn(candidate)) -> {
                    removedCount += 1
                }
                normalizedIsbn(candidate) in readIsbns -> {
                    removedCount += 1
                }
                else -> filtered += candidate
            }
        }

        return FilterResult(
            candidates = filtered,
            removedCount = removedCount
        )
    }

    private fun hasRequiredMetadata(candidate: RecommendationCandidate): Boolean {
        val item = candidate.item
        return !item.isbn.isNullOrBlank() &&
            !item.title.isNullOrBlank() &&
            !item.author.isNullOrBlank() &&
            !item.description.isNullOrBlank() &&
            !item.publisher.isNullOrBlank() &&
            !item.pubDate.isNullOrBlank()
    }

    private fun normalizedIsbn(candidate: RecommendationCandidate): String {
        return candidate.item.isbn13?.takeIf { it.isNotBlank() }
            ?: candidate.item.isbn?.trim().orEmpty()
    }
}
