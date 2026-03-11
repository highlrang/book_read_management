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

        val filtered = candidates
            .filter(::hasRequiredMetadata)
            .distinctBy { normalizedIsbn(it) }
            .filterNot { normalizedIsbn(it) in readIsbns }

        return FilterResult(
            candidates = filtered,
            removedCount = candidates.size - filtered.size
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
