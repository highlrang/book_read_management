package com.liber.book_read_management.dto

import com.liber.book_read_management.enums.BookPageType

class BookReadPageUpdateRequest (
    var bookReadLogId: Long,
    var type: BookPageType,
    var page: Int,
)