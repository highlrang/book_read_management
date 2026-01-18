package com.liber.book_read_management.controller

import com.liber.book_read_management.auth.CurrentUserId
import com.liber.book_read_management.dto.*
import com.liber.book_read_management.service.BookReadLogService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

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

    /**
     * TODO 페이지 갱신 모달창!!
     */
    @Operation(summary = "도서 페이지 업데이트", description = "도서 전체 페이지(TOTAL) 또는 읽은 페이지(READ)를 업데이트합니다.")
    @PatchMapping("/page")
    fun updatePage(@CurrentUserId userId: Long, @Valid @RequestBody readPageUpdateRequest: BookReadPageUpdateRequest) : ResponseEntity<ApiResponse<Unit>> {
        bookReadLogService.updatePage(userId, readPageUpdateRequest)
        return ResponseEntity.ok(ApiResponse.success())
    }

    @Operation(summary = "도서 읽기 기록 목록 조회", description = "도서 읽기 기록 목록을 조회합니다.")
    @GetMapping
    fun getBookReadLogs(@CurrentUserId userId: Long,
                        readLogSearchRequest: BookReadLogSearchRequest,
                        pageable: Pageable
    ) : ResponseEntity<ApiResponse<List<BookReadLogResponse>>> {
        val pageRequest = PageRequest.of(pageable.pageNumber, pageable.pageSize, pageable.sort)
        val bookReadLogResponseList = bookReadLogService.searchBookReadLogs(userId, readLogSearchRequest, pageRequest)
        return ResponseEntity.ok(ApiResponse.success(bookReadLogResponseList))
    }

    @Operation(summary = "도서 읽기 기록 상세 조회", description = "도서 읽기 기록 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    fun getBookReadLogs(@CurrentUserId userId: Long,
                        bookReadLogId: Long) : ResponseEntity<ApiResponse<BookReadLogResponse>> {
        val bookReadLogResponse = bookReadLogService.getBookReadLog(userId, bookReadLogId)
        return ResponseEntity.ok(ApiResponse.success(bookReadLogResponse))
    }

}