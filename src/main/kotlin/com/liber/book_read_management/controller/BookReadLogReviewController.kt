package com.liber.book_read_management.controller

import com.liber.book_read_management.auth.CurrentUserId
import com.liber.book_read_management.dto.ApiResponse
import com.liber.book_read_management.dto.BookReviewSaveRequest
import com.liber.book_read_management.service.BookReadLogService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

class BookReadLogReviewController(
    val bookReadLogService: BookReadLogService
) {

    @PostMapping("/review")
    fun saveReview(@CurrentUserId userId: Long, @Valid @RequestBody reviewSaveRequest: BookReviewSaveRequest) : ResponseEntity<ApiResponse<Unit>> {
        bookReadLogService.saveBookReview(userId, reviewSaveRequest)
        return ResponseEntity.ok(ApiResponse.success())
    }
}