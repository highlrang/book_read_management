package com.liber.book_read_management.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "도서 읽기 기록 저장 요청")
class BookReadLogSaveRequest (
    @Schema(description = "도서 ISBN", example = "9788960777330")
    var bookSbn: String
)
