package com.liber.book_read_management.dto

class BookRatingUpdateRequest(
    val bookReadLogId: Long,
    val rating: Int?,
    val content: String?
)