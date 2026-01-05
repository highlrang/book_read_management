package com.liber.book_read_management.controller

import com.liber.book_read_management.auth.CurrentUserId
import com.liber.book_read_management.dto.ApiResponse
import com.liber.book_read_management.dto.BookRatingSaveRequest
import com.liber.book_read_management.dto.BookRatingUpdateRequest
import com.liber.book_read_management.service.BookReadLogService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class BookReadLogRatingController(
    val bookReadLogService: BookReadLogService
) {

    @PostMapping("rating")
    fun saveRating(@CurrentUserId userId: Long, @Valid @RequestBody ratingSaveRequest: BookRatingSaveRequest) : ResponseEntity<ApiResponse<Unit>> {
        bookReadLogService.saveBookRating(userId, ratingSaveRequest)
        return ResponseEntity.ok(ApiResponse.success())
    }

    @PatchMapping("rating")
    fun updateRating(@CurrentUserId userId: Long, ratingUpdateRequest: BookRatingUpdateRequest) : ResponseEntity<ApiResponse<Unit>> {
        bookReadLogService.updateBookRating(userId, ratingUpdateRequest)
        return ResponseEntity.ok(ApiResponse.success())
    }

}