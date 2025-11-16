package com.liber.book_read_management.dto

import com.liber.book_read_management.dto.aladin.AladinBookDetailResponse

class BookDetailResponse (
    val logo: String?,
    val cover: String?,
    val link: String?,

    val title: String?,
    val description: String?,
    val author: String?,
    val pubDate: String?,
    val isbn: String?,

    val categoryId: Int?,
    val categoryName: String?,
    val publisher: String?,
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
                publisher = item?.publisher,
                itemPage = subInfo?.itemPage
            )
        }
    }
}