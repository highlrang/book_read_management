package com.liber.book_read_management.dto

class BookSearchRequest(
    val query: String?,
    val page: Int = 1,
    val size: Int = 100,
) {
}