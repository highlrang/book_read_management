package com.liber.book_read_management.controller

import com.liber.book_read_management.auth.CurrentUserId
import com.liber.book_read_management.dto.ApiResponse
import com.liber.book_read_management.dto.BookReadLogResponse
import com.liber.book_read_management.dto.BookReadLogSaveRequest
import com.liber.book_read_management.dto.BookReadPageUpdateRequest
import com.liber.book_read_management.service.BookReadLogService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/book-read")
class BookReadLogApiController (
    var bookReadLogService: BookReadLogService
) {

    @PostMapping
    fun saveReadLog(@CurrentUserId userId: Long, @Valid @RequestBody readLogSaveRequest: BookReadLogSaveRequest) : ResponseEntity<ApiResponse<BookReadLogResponse>> {
        val bookReadLogId = bookReadLogService.saveBookReadLog(userId, readLogSaveRequest)
        return ResponseEntity.ok(ApiResponse.success(BookReadLogResponse(bookReadLogId)))
    }

    @PostMapping
    fun updatePage(@CurrentUserId userId: Long, @Valid @RequestBody readPageUpdateRequest: BookReadPageUpdateRequest) : ResponseEntity<ApiResponse<Unit>> {
        bookReadLogService.updatePage(userId, readPageUpdateRequest)
        return ResponseEntity.ok(ApiResponse.success())
    }
}