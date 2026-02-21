package com.liber.book_read_management.dto

import com.liber.book_read_management.enums.BookPageType
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min

@Schema(description = "도서 페이지 업데이트 요청")
class BookReadPageUpdateRequest (
    @Schema(description = "도서 읽기 기록 ID", example = "1")
    var bookReadLogId: Long,
    @Schema(description = "페이지 타입", example = "TOTAL")
    var type: BookPageType,
    @Schema(description = "페이지", example = "100")
    @Min(0)
    var page: Int,
)