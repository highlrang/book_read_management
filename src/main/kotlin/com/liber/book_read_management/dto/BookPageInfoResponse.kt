package com.liber.book_read_management.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "도서 페이지 정보 응답")
class BookPageInfoResponse(
    @Schema(description = "ISBN", example = "9788937834790")
    val isbn: String,
    @Schema(description = "전체 페이지 수", example = "432")
    val totalPage: Int?
)
