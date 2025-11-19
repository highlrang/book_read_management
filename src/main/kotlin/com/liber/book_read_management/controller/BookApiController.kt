package com.liber.book_read_management.controller

import com.liber.book_read_management.dto.ApiResponse
import com.liber.book_read_management.dto.BookDetailResponse
import com.liber.book_read_management.dto.BookPageResponse
import com.liber.book_read_management.dto.BookSearchRequest
import com.liber.book_read_management.dto.PageResponse
import com.liber.book_read_management.service.BookService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/book")
class BookApiController(
    var bookService: BookService
) {

    @GetMapping
    fun searchBook(
        bookSearchRequest: BookSearchRequest
    ) : ResponseEntity<ApiResponse<PageResponse>> {
        val bookPageResponse = bookService.searchBook(bookSearchRequest)
        return ResponseEntity.ok(ApiResponse.success(bookPageResponse))
    }

    @GetMapping("/{isbn}")
    fun getBookDetail(@PathVariable("isbn") isbn: String) : ResponseEntity<ApiResponse<BookDetailResponse>> {
        val bookResponse = bookService.getBookDetail(isbn)
        return ResponseEntity.ok(ApiResponse.success(bookResponse))
    }
}