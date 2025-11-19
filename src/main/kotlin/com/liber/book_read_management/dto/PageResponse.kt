package com.liber.book_read_management.dto

class PageResponse<T>(
    var page: Int,
    var size: Int,

    var totalResults: Int?,
    var totalPage: Int?,

    var contents: T,
) {

}