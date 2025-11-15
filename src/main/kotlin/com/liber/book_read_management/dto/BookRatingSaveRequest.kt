package com.liber.book_read_management.dto

import com.liber.book_read_management.entities.BookRatingLog

class BookRatingSaveRequest(
    val bookReadLogId: Long,
    val rating: Int,
    val content: String
) {

    fun toEntity() : BookRatingLog {
        return BookRatingLog(
            bookReadLogId = bookReadLogId,
            rating = rating,
            content = content
        )
    }
}