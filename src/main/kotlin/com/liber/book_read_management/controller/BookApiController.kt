package com.liber.book_read_management.controller

import com.liber.book_read_management.dto.*
import com.liber.book_read_management.service.BookService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "도서 API", description = "도서 관련 API")
@RestController
@RequestMapping("/api/v1/book")
class BookApiController(
    var bookService: BookService
) {

    @Operation(
        summary = "도서 검색",
        description = "알라딘 API로 도서를 검색합니다. 검색어가 없으면 BestSeller를 조회합니다. " +
            "응답의 표지는 네이버 책 검색 이미지로 보강될 수 있습니다.\n" +
            "페이지당 사이즈는 20으로 고정입니다."
    )
    @GetMapping
    fun searchBook(
        @ParameterObject bookSearchRequest: BookSearchRequest
    ) : ResponseEntity<ApiResponse<PageResponse<List<BookSearchResponse>>>> {
        val bookPageResponse = bookService.searchBook(bookSearchRequest)
        return ResponseEntity.ok(ApiResponse.success(bookPageResponse))
    }

    @Operation(summary = "도서 카테고리 목록 조회", description = "도서 카테고리 목록을 조회합니다. (서버 임의 카테고리)")
    @GetMapping("/category")
    fun getBookCategories() : ResponseEntity<ApiResponse<List<BookCategoryResponse>>> {
        return ResponseEntity.ok(ApiResponse.success(bookService.getBookCategories()))
    }

    @Deprecated("Use /api/v1/book/{isbn}/page-info for total page lookup and use list response data for book metadata.")
    @Operation(summary = "도서 상세 조회", description = "ISBN으로 도서 상세 정보를 조회합니다.", deprecated = true)
    @GetMapping("/{isbn}")
    fun getBookDetail(@PathVariable("isbn") isbn: String) : ResponseEntity<ApiResponse<BookDetailResponse>> {
        val bookResponse = bookService.getBookDetail(isbn)
        return ResponseEntity.ok(ApiResponse.success(bookResponse))
    }

    @Operation(summary = "도서 페이지 수 조회", description = "ISBN으로 도서 전체 페이지 수를 조회합니다.")
    @GetMapping("/{isbn}/page-info")
    fun getBookPageInfo(@PathVariable("isbn") isbn: String) : ResponseEntity<ApiResponse<BookPageInfoResponse>> {
        val bookPageInfoResponse = bookService.getBookPageInfo(isbn)
        return ResponseEntity.ok(ApiResponse.success(bookPageInfoResponse))
    }
}
