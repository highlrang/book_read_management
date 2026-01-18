package com.liber.book_read_management.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "책 검색 요청")
class BookSearchRequest(
    @Schema(description = "검색어", example = "코틀린")
    val query: String?,
    @Schema(description = "페이지 번호", example = "1")
    val page: Int = 1,
    @Schema(description = "페이지 사이즈", example = "10")
    val size: Int = 10,
) {
}