package com.liber.book_read_management.dto

class PageResponse<T>(
    var page: Int,
    var size: Int,

    var totalPage: Int?,

    var contents: T,

) {

    companion object {

        fun calTotalPage(totalCount: Int? = 0, size: Int? = 0) : Int {
            if (size == 0 || totalCount == 0)
                return 0

            return if (totalCount!! % size!! == 0) {
                totalCount / size
            } else {
                (totalCount / size) + 1
            }
        }
    }
}