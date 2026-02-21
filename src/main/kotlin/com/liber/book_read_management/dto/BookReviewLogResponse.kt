package com.liber.book_read_management.dto

import java.time.LocalDate

class BookReviewLogResponse(
    val readPage: Int,
    val content: String,
    val date: LocalDate
)