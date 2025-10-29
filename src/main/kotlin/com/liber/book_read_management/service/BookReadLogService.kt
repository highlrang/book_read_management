package com.liber.book_read_management.service

import com.liber.book_read_management.dto.BookReadLogSaveRequest
import com.liber.book_read_management.dto.BookReadPageUpdateRequest

interface BookReadLogService {

    fun saveBookReadLog(userId: Long, readLogSaveRequest: BookReadLogSaveRequest) : Long
    fun updatePage(userId: Long, readPageUpdateRequest: BookReadPageUpdateRequest)
}