package com.liber.book_read_management.dto

import java.time.LocalDate

class BookReadPageResponse (
    val prevReadPage: Int,
    val readPage: Int,
    val pageDiff: Int,
    val date: LocalDate
)