package com.liber.book_read_management.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.liber.book_read_management.entities.BookReadLog
import com.liber.book_read_management.enums.BookReadStatus
import com.querydsl.core.annotations.QueryProjection
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

/**
 * 목록용
 */
@Schema(description = "도서 읽기 기록 응답")
open class BookReadLogResponse @QueryProjection constructor(
    @Schema(description = "ID", example = "1")
    var id: Long,
    @Schema(description = "도서 ISBN", example = "9788960777330")
    var bookIsbn: String,
    @Schema(description = "도서 제목", example = "Kotlin in Action")
    var bookTitle: String,
    @Schema(description = "도서 저자", example = "드미트리 제메로프, 스베트라나 이사코바 (지은이), 오현석 (옮긴이)")
    var bookAuthor: String,
    @Schema(description = "도서 썸네일 이미지", example = "https://image.aladin.co.kr/product/3020/9/cover/8960777331_1.jpg")
    var bookThumbnailImage: String,
    @Schema(description = "전체 페이지", example = "600")
    var totalPage: Int,
    @Schema(description = "읽은 페이지", example = "100")
    var readPage: Int,
    @Schema(description = "읽기 상태", example = "READING")
    var readStatus: BookReadStatus,
    @Schema(description = "진행률", example = "16")
    var progressPercentage: Int,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "생성일", example = "2026-01-01 12:00:00")
    var createdAt: LocalDateTime,
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "수정일", example = "2026-01-01 12:00:00")
    var updatedAt: LocalDateTime
) {
    companion object {
        fun from(bookReadLog: BookReadLog) : BookReadLogResponse {
            return BookReadLogResponse(
                id = bookReadLog.id!!,
                bookIsbn = bookReadLog.bookIsbn,
                bookTitle = bookReadLog.bookTitle,
                bookAuthor = bookReadLog.bookAuthor,
                bookThumbnailImage = bookReadLog.bookThumbnailImage,
                totalPage = bookReadLog.totalPage,
                readPage = 0,
                readStatus = bookReadLog.readStatus,
                progressPercentage = bookReadLog.progressPercentage,
                createdAt = bookReadLog.createdAt!!,
                updatedAt = bookReadLog.updatedAt!!
            )
        }
    }
}
