package com.liber.book_read_management.dto

class PageResponse<T>(
    var page: Int,
    var size: Int,

    var totalPage: Int,
    var totalElements: Long,

    var content: T,

) {

    companion object {

        fun calTotalPage(totalCount: Int?, size: Int?) : Int {
            val count = totalCount ?: 0
            val pageSize = size ?: 10

            if (pageSize <= 0 || count <= 0) return 0

            return (count + pageSize - 1) / pageSize
        }
    }
}