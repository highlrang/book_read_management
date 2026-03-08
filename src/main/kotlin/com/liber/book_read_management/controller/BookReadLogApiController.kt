package com.liber.book_read_management.controller

import com.liber.book_read_management.auth.CurrentUserId
import com.liber.book_read_management.dto.*
import com.liber.book_read_management.service.BookReadLogService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "도서 읽기 기록 API", description = "도서 읽기 기록 관련 API")
@RestController
@RequestMapping("/api/v1/book-read")
class BookReadLogApiController (
    var bookReadLogService: BookReadLogService
) {

    @Operation(summary = "도서 읽기 기록 저장", description = "도서 읽기 기록을 저장합니다.")
    @PostMapping
    fun saveReadLog(@CurrentUserId userId: Long, @Valid @RequestBody readLogSaveRequest: BookReadLogSaveRequest) : ResponseEntity<ApiResponse<BookReadLogResponse>> {
        val bookReadLogResponse = bookReadLogService.saveBookReadLog(userId, readLogSaveRequest)
        return ResponseEntity.ok(ApiResponse.success(bookReadLogResponse))
    }

    @Operation(summary = "도서 읽기 기록 목록 조회", description = "도서 읽기 기록 목록을 조회합니다.")
    @GetMapping
    fun getBookReadLogs(@CurrentUserId userId: Long,
                        @ParameterObject request: BookReadLogSearchRequest
    ) : ResponseEntity<ApiResponse<PageResponse<List<BookReadLogResponse>>>> {
        val bookReadLogResponseList = bookReadLogService.searchBookReadLogs(userId, request)
        return ResponseEntity.ok(ApiResponse.success(bookReadLogResponseList))
    }

    @Operation(summary = "도서 읽기 기록 상세 조회", description = "도서 읽기 기록 상세 정보를 조회합니다.")
    @GetMapping("/{bookReadLogId}")
    fun getBookReadLog(@CurrentUserId userId: Long,
                       @PathVariable bookReadLogId: Long) : ResponseEntity<ApiResponse<BookReadLogDetailResponse>> {
        val response = bookReadLogService.getBookReadLog(userId, bookReadLogId)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

}
