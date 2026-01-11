package com.liber.book_read_management.dto

import com.fasterxml.jackson.annotation.JsonFormat
import com.liber.book_read_management.entities.BookReadLog
import com.liber.book_read_management.enums.BookReadStatus
import java.time.LocalDateTime

/**
 * 목록용
 */
class BookReadLogResponse (
    var id: Long,

    var bookIsbn: String,

    var bookTitle: String,

    var bookAuthor: String,

    var bookThumbnailImage: String,

    var readStatus: BookReadStatus,

    var progressPercentage: Int,

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    var createdAt: LocalDateTime,

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
                readStatus = bookReadLog.readStatus,
                progressPercentage = bookReadLog.progressPercentage,
                createdAt = bookReadLog.createdAt!!,
                updatedAt = bookReadLog.updatedAt!!
            )
        }
    }
}