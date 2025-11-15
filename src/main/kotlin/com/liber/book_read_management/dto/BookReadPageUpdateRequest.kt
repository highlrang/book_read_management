package com.liber.book_read_management.dto

import com.liber.book_read_management.enums.PageType

class BookReadPageUpdateRequest (
    var bookReadLogId: Long,
    var type: PageType,
    var page: Int,
)