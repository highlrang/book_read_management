package com.liber.book_read_management.controller

import com.liber.book_read_management.auth.CurrentUserId
import com.liber.book_read_management.dto.ApiResponse
import com.liber.book_read_management.dto.BookReadPageResponse
import com.liber.book_read_management.dto.BookReadPageUpdateRequest
import com.liber.book_read_management.service.BookReadLogService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "내 서재 도서 페이지 기록 API")
@RestController
@RequestMapping("/api/v1/book-read")
class BookReadLogPageApiController(
    val bookReadLogService: BookReadLogService
) {

    @Operation(summary = "도서 페이지 업데이트 API", description = "도서 전체 페이지(TOTAL) 또는 읽은 페이지(READ)를 업데이트합니다.")
    @PatchMapping("/page/{bookReadLogId}")
    fun updatePage(@CurrentUserId userId: Long,
                   @PathVariable bookReadLogId: Long,
                   @Valid @RequestBody readPageUpdateRequest: BookReadPageUpdateRequest) : ResponseEntity<ApiResponse<Unit>> {
        bookReadLogService.updatePage(userId, bookReadLogId, readPageUpdateRequest)
        return ResponseEntity.ok(ApiResponse.success())
    }

    @Operation(summary = "도서 읽기 기록 조회 API", description = "도서 읽은 페이지 히스토리를 조회합니다.")
    @GetMapping("/page/{bookReadLogId}")
    fun getBookReadPageHistory(@CurrentUserId userId: Long,
                               @PathVariable bookReadLogId: Long) : ResponseEntity<ApiResponse<List<BookReadPageResponse>>> {
        return ResponseEntity.ok(
            ApiResponse.success(bookReadLogService.getReadPageHistory(userId, bookReadLogId))
        )
    }
}
