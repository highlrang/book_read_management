package com.liber.book_read_management.dto.semantic

data class BookInfoRequest(
    val title: String,
    val introduction: String,
    val reviews: List<String>
)
