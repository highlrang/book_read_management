package com.liber.book_read_management.controller

import com.liber.book_read_management.auth.CurrentUserId
import com.liber.book_read_management.dto.BookSearchRequest
import com.liber.book_read_management.dto.BookSearchResponse
import com.liber.book_read_management.dto.PageResponse
import com.liber.book_read_management.dto.SemanticSearchRequest
import com.liber.book_read_management.dto.semantic.BookInfoRequest
import com.liber.book_read_management.exception.ApiException
import com.liber.book_read_management.exception.ExceptionType
import com.liber.book_read_management.repository.BookReadLogRepository
import com.liber.book_read_management.repository.BookReviewLogRepository
import com.liber.book_read_management.service.BookService
import com.liber.book_read_management.service.SemanticService
import lombok.RequiredArgsConstructor
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/semantic")
@RequiredArgsConstructor
class SemanticController(
    private val semanticService: SemanticService,
    private val bookReadLogRepository: BookReadLogRepository,
    private val bookReadReviewRepository: BookReviewLogRepository,
    private val bookService: BookService
) {

    @PostMapping("/recommend")
    fun generateSemanticScore(
        @CurrentUserId userId: Long,
        @RequestBody request: SemanticSearchRequest
    ): PageResponse<List<BookSearchResponse>> {
        val bookReadLog = bookReadLogRepository.findByUserIdAndId(userId, request.bookReadLogId)
            ?: throw ApiException(ExceptionType.DATA_NOT_FOUND)

        val reviews = bookReadReviewRepository.findAllByBookReadLogId(bookReadLog.id!!);

        val bookInfoRequest = BookInfoRequest(
            title = bookReadLog.bookTitle,
            introduction = bookReadLog.bookIsbn,
            reviews = reviews.map { it.content }
        )

        val bookSearchQuery = semanticService.generateSemanticQuery(bookInfoRequest)
        return bookService.searchBook(BookSearchRequest(query = bookSearchQuery?.joinToString { "" }, page = 0, size = 10))
    }
}