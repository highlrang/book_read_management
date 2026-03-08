package com.liber.book_read_management.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "도서 카테고리 응답")
data class BookCategoryResponse(
    @Schema(description = "카테고리 코드", example = "TECHNOLOGY")
    val code: String,
    @Schema(description = "카테고리명", example = "기술/IT")
    val label: String
)
