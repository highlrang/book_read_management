package com.liber.book_read_management.controller

import com.liber.book_read_management.auth.CurrentUserId
import com.liber.book_read_management.dto.*
import com.liber.book_read_management.service.BookReadLogService
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/book-read")
class BookReadLogApiController (
    var bookReadLogService: BookReadLogService
) {

    @PostMapping
    fun saveReadLog(@CurrentUserId userId: Long, @Valid @RequestBody readLogSaveRequest: BookReadLogSaveRequest) : ResponseEntity<ApiResponse<BookReadLogResponse>> {
        val bookReadLogResponse = bookReadLogService.saveBookReadLog(userId, readLogSaveRequest)
        return ResponseEntity.ok(ApiResponse.success(bookReadLogResponse))
    }

    /**
     * TODO 페이지 갱신 모달창!!
     */
    @PostMapping("/page")
    fun updatePage(@CurrentUserId userId: Long, @Valid @RequestBody readPageUpdateRequest: BookReadPageUpdateRequest) : ResponseEntity<ApiResponse<Unit>> {
        bookReadLogService.updatePage(userId, readPageUpdateRequest)
        return ResponseEntity.ok(ApiResponse.success())
    }

    @GetMapping
    fun getBookReadLogs(@CurrentUserId userId: Long,
                        readLogSearchRequest: BookReadLogSearchRequest,
                        pageRequest: PageRequest) : ResponseEntity<ApiResponse<List<BookReadLogResponse>>> {
        val bookReadLogResponse = bookReadLogService.searchBookReadLogs(userId, readLogSearchRequest, pageRequest)
        return ResponseEntity.ok(ApiResponse.success(bookReadLogResponse))
    }

}