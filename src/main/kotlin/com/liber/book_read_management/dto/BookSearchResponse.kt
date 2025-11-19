package com.liber.book_read_management.dto

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
}