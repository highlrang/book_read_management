package com.liber.book_read_management.dto

import com.liber.book_read_management.entities.BookReviewLog

class BookReviewSaveRequest(
    var bookReadLogId: Long,
    var readPage: Int,
    var content: String,
) {
    fun toEntity() : BookReviewLog {
        return BookReviewLog(
            bookReadLogId = bookReadLogId,
            readPage = readPage,
            content = content
        )
    }
}