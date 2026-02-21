package com.liber.book_read_management.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "도서 읽기 기록 저장 요청")
class BookReadLogSaveRequest (
    @Schema(description = "도서 ISBN", example = "9788960777330")
    var bookSbn: String,
    @Schema(description = "도서 제목", example = "Kotlin in Action")
    var bookTitle: String,
    @Schema(description = "도서 저자", example = "드미트리 제메로프, 스베트라나 이사코바 (지은이), 오현석 (옮긴이)")
    var bookAuthor: String,
    @Schema(description = "도서 썸네일 이미지", example = "https://image.aladin.co.kr/product/3020/9/cover/8960777331_1.jpg")
    var bookThumbnailImage: String,
    @Schema(description = "도서 전체 페이지", example = "312")
    val bookTotalPage: Int?
)