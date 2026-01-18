package com.liber.book_read_management.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "책 평점 수정 요청")
class BookRatingUpdateRequest(
    @Schema(description = "책 읽기 기록 ID", example = "1")
    val bookReadLogId: Long,
    @Schema(description = "평점", example = "4")
    val rating: Int?,
    @Schema(description = "내용", example = "생각보다 별로에요")
    val content: String?
)