package com.liber.book_read_management.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min

@Schema(description = "도서 평점 수정 요청")
class BookRatingUpdateRequest(
    @Schema(description = "도서 읽기 기록 ID", example = "1")
    val bookReadLogId: Long,
    @Min(0) @Max(5)
    @Schema(description = "평점", example = "3")
    val rating: Int?,
    @Schema(description = "내용", example = "쉽게 읽힌다.")
    val content: String?
)