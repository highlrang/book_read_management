package com.liber.book_read_management.controller

import com.liber.book_read_management.auth.CurrentUserId
import com.liber.book_read_management.dto.BookSearchResponse
import com.liber.book_read_management.dto.PageResponse
import com.liber.book_read_management.dto.semantic.BookInfoRequest
import com.liber.book_read_management.exception.ApiException
import com.liber.book_read_management.exception.ExceptionType
import com.liber.book_read_management.entities.RecommendationSourceType
import com.liber.book_read_management.repository.BookReadLogRepository
import com.liber.book_read_management.repository.BookReviewLogRepository
import com.liber.book_read_management.service.recommendation.RecommendationAgentService
import com.liber.book_read_management.service.recommendation.model.RecommendationSourceLog
import lombok.RequiredArgsConstructor
import org.springframework.context.annotation.Profile
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Profile("local")
@RestController
@RequestMapping("/api/v1/semantic")
@RequiredArgsConstructor
class SemanticApiController(
    private val recommendationAgentService: RecommendationAgentService,
    private val bookReadLogRepository: BookReadLogRepository,
    private val bookReadReviewRepository: BookReviewLogRepository
) {

    @PostMapping("/recommend")
    fun recommendByUser(
        @CurrentUserId userId: Long
    ): PageResponse<List<BookSearchResponse>> {
        val recentLogs = bookReadLogRepository.findTop10ByUserIdOrderByIdDesc(userId)
        if (recentLogs.isEmpty()) {
            throw ApiException(ExceptionType.DATA_NOT_FOUND)
        }

        val logIds = recentLogs.mapNotNull { it.id }
        val reviews = if (logIds.isEmpty()) {
            emptyList()
        } else {
            bookReadReviewRepository.findAllByUserIdAndBookReadLogIdInOrderByIdDesc(userId, logIds)
        }

        val title = recentLogs
            .map { it.bookTitle }
            .distinct()
            .take(3)
            .joinToString(" ")

        val introduction = recentLogs
            .mapNotNull { log ->
                val parts = listOfNotNull(log.bookAuthor, log.categoryPath)
                if (parts.isEmpty()) null else parts.joinToString(" | ")
            }
            .distinct()
            .take(5)
            .joinToString(" / ")

        val bookInfoRequest = BookInfoRequest(
            title = title,
            introduction = introduction.ifBlank { title },
            reviews = reviews.map { it.content }
        )

        return recommendationAgentService.recommend(
            userId = userId,
            request = bookInfoRequest,
            sourceLogs = logIds.map { RecommendationSourceLog(it, RecommendationSourceType.RECENT_USER_LOG) }
        )
    }

    @PostMapping("/recommend/{bookReadLogId}")
    fun recommendByBookReadLog(
        @CurrentUserId userId: Long,
        @PathVariable bookReadLogId: Long
    ): PageResponse<List<BookSearchResponse>> {
        val bookReadLog = bookReadLogRepository.findByUserIdAndId(userId, bookReadLogId)
            ?: throw ApiException(ExceptionType.DATA_NOT_FOUND)

        val reviews = bookReadReviewRepository.findAllByUserIdAndBookReadLogIdOrderByIdDesc(userId, bookReadLog.id!!)

        val bookInfoRequest = BookInfoRequest(
            title = bookReadLog.bookTitle,
            introduction = listOfNotNull(bookReadLog.bookAuthor, bookReadLog.categoryPath)
                .joinToString(" | ")
                .ifBlank { bookReadLog.bookTitle },
            reviews = reviews.map { it.content }
        )

        return recommendationAgentService.recommend(
            userId = userId,
            request = bookInfoRequest,
            sourceLogs = listOf(RecommendationSourceLog(bookReadLog.id!!, RecommendationSourceType.TARGET_LOG))
        )
    }
}
