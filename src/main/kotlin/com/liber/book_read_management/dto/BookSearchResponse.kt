package com.liber.book_read_management.dto

import com.liber.book_read_management.dto.aladin.AladinBookSearchResponse
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "도서 검색 응답")
class BookSearchResponse(
    @Schema(description = "ISBN", example = "9788960777330")
    var isbn: String?,
    @Schema(description = "표지", example = "https://image.aladin.co.kr/product/3020/9/cover/8960777331_1.jpg")
    var cover: String?,
    @Schema(description = "제목", example = "Kotlin in Action")
    var title: String?,
    @Schema(description = "저자", example = "드미트리 제메로프, 스베트라나 이사코바 (지은이), 오현석 (옮긴이)")
    var author: String?,
    @Schema(description = "설명", example = "코틀린의 내부 구조와 설계 철학을 깊이 있게 다루고, 코틀린으로 실용적인 애플리케이션을 개발하는 방법을 알려준다.")
    var description: String?,
    @Schema(description = "출판사", example = "에이콘출판")
    var publisher: String?,
    @Schema(description = "출판일", example = "2017-09-01")
    var pubDate: String?,
    @Schema(description = "링크", example = "http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=116520102&amp;partner=openAPI&amp;start=api")
    var link: String?,

) {

    companion object {
        fun of(aladinItem: AladinBookSearchResponse.AladinItem) : BookSearchResponse {
            return BookSearchResponse(
                aladinItem.isbn,
                aladinItem.cover,
                aladinItem.title,
                aladinItem.author,
                aladinItem.description,
                aladinItem.publisher,
                aladinItem.pubDate,
                aladinItem.link
            )
        }
    }

}