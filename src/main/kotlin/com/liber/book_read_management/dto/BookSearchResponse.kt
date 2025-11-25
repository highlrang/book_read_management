package com.liber.book_read_management.dto

import com.liber.book_read_management.dto.aladin.AladinBookSearchResponse

class BookSearchResponse(
    var isbn: String?,
    var cover: String?,
    var title: String?,
    var author: String?,

    var description: String?,
    var publisher: String?,
    var pubDate: String?,

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