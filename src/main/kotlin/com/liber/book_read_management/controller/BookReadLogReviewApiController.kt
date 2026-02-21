package com.liber.book_read_management.controller

import com.liber.book_read_management.auth.CurrentUserId
import com.liber.book_read_management.dto.ApiResponse
import com.liber.book_read_management.dto.BookReviewLogResponse
import com.liber.book_read_management.dto.BookReviewSaveRequest
import com.liber.book_read_management.service.BookReadLogService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "도서 리뷰 API", description = "도서 리뷰 관련 API")
@RestController
@RequestMapping("/api/v1/book-read")
class BookReadLogReviewApiController(
    val bookReadLogService: BookReadLogService
) {

    @Operation(summary = "도서 리뷰 저장", description = "도서 페이지와 함께 리뷰를 저장합니다.")
    @PostMapping("/review")
    fun saveReview(@CurrentUserId userId: Long, @Valid @RequestBody reviewSaveRequest: BookReviewSaveRequest) : ResponseEntity<ApiResponse<Unit>> {
        bookReadLogService.saveBookReview(userId, reviewSaveRequest)
        return ResponseEntity.ok(ApiResponse.success())
    }

    @Operation(summary = "도서 리뷰 조회")
    @GetMapping("/reviews/{bookReadLogId}")
    fun getReviews(@CurrentUserId userId: Long, @PathVariable bookReadLogId: Long) : ResponseEntity<ApiResponse<List<BookReviewLogResponse>>> {
        return ResponseEntity.ok(
            ApiResponse.success(bookReadLogService.getBookReviewLogs(userId, bookReadLogId))
        )
    }
}