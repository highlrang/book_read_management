package com.liber.book_read_management.dto

import com.liber.book_read_management.dto.aladin.AladinBookDetailResponse
import com.liber.book_read_management.util.CategoryClassifier
import com.liber.book_read_management.util.CategoryLabel
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "도서 상세 응답")
class BookDetailResponse (
    @Schema(description = "로고", example = "https://image.aladin.co.kr/img/header/2011/aladin_logo.gif")
    val logo: String?,
    @Schema(description = "표지", example = "https://image.aladin.co.kr/product/3020/9/cover/8960777331_1.jpg")
    val cover: String?,
    @Schema(description = "링크", example = "http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=116520102&amp;partner=openAPI&amp;start=api")
    val link: String?,
    @Schema(description = "제목", example = "Kotlin in Action")
    val title: String?,
    @Schema(description = "설명", example = "코틀린의 내부 구조와 설계 철학을 깊이 있게 다루고, 코틀린으로 실용적인 애플리케이션을 개발하는 방법을 알려준다.")
    val description: String?,
    @Schema(description = "저자", example = "드미트리 제메로프, 스베트라나 이사코바 (지은이), 오현석 (옮긴이)")
    val author: String?,
    @Schema(description = "출판일", example = "2017-09-01")
    val pubDate: String?,
    @Schema(description = "ISBN", example = "9788960777330")
    val isbn: String?,
    @Schema(description = "카테고리 ID", example = "51356")
    val categoryId: Int?,
    @Schema(description = "카테고리명", example = "국내도서>컴퓨터/모바일>프로그래밍 언어>자바")
    val categoryName: String?,
    @Schema(description = "카테고리", example = "기술/IT")
    val category: String,
    @Schema(description = "출판사", example = "에이콘출판")
    val publisher: String?,
    @Schema(description = "페이지 수", example = "600")
    val itemPage: Int?

){

    companion object {
        fun of(aladinBookDetail: AladinBookDetailResponse) : BookDetailResponse {
            val item = aladinBookDetail.item?.firstOrNull()
            val subInfo = item?.subInfo

            return BookDetailResponse(
                logo = aladinBookDetail.logo,
                cover = item?.cover,
                link = aladinBookDetail.link,
                title = item?.title,
                description = item?.description,
                author = item?.author,
                pubDate = item?.pubDate,
                isbn = item?.isbn,
                categoryId = item?.categoryId,
                categoryName = item?.categoryName,
                category = CategoryLabel.toKorean(CategoryClassifier.classify(item?.categoryName)),
                publisher = item?.publisher,
                itemPage = subInfo?.itemPage
            )
        }
    }
}
