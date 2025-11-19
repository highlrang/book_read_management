package com.liber.book_read_management.service

import com.liber.book_read_management.dto.BookDetailResponse
import com.liber.book_read_management.dto.BookPageResponse
import com.liber.book_read_management.dto.BookSearchRequest
import com.liber.book_read_management.dto.PageResponse

interface BookService {

    fun searchBook(bookSearchRequest: BookSearchRequest) : PageResponse

    fun getBookDetail(isbn: String) : BookDetailResponse
}