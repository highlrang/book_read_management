package com.liber.book_read_management.controller

import com.liber.book_read_management.auth.CurrentUserId
import com.liber.book_read_management.dto.ApiResponse
import com.liber.book_read_management.dto.BookRatingSaveRequest
import com.liber.book_read_management.dto.BookRatingUpdateRequest
import com.liber.book_read_management.service.BookReadLogService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "도서 평점 API", description = "완독 후 도서 평점 관련 API")
@RestController
@RequestMapping("/api/v1/book-read")
class BookReadLogRatingApiController(
    val bookReadLogService: BookReadLogService
) {

    @Operation(summary = "도서 평점 저장", description = "도서 평점과 리뷰를 저장합니다.")
    @PostMapping("rating")
    fun saveRating(@CurrentUserId userId: Long, @Valid @RequestBody ratingSaveRequest: BookRatingSaveRequest) : ResponseEntity<ApiResponse<Unit>> {
        bookReadLogService.saveBookRating(userId, ratingSaveRequest)
        return ResponseEntity.ok(ApiResponse.success())
    }

    @Operation(summary = "도서 평점 수정", description = "도서 평점과 리뷰를 수정합니다.")
    @PatchMapping("rating")
    fun updateRating(@CurrentUserId userId: Long, ratingUpdateRequest: BookRatingUpdateRequest) : ResponseEntity<ApiResponse<Unit>> {
        bookReadLogService.updateBookRating(userId, ratingUpdateRequest)
        return ResponseEntity.ok(ApiResponse.success())
    }

}