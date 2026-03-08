package com.liber.book_read_management.service

import com.liber.book_read_management.dto.*

interface BookService {

    fun searchBook(bookSearchRequest: BookSearchRequest) : PageResponse<List<BookSearchResponse>>
    fun getBookCategories() : List<BookCategoryResponse>

    fun getBookDetail(isbn: String) : BookDetailResponse
}
