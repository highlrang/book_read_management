package com.liber.book_read_management.repository.query;

import com.liber.book_read_management.dto.BookReadLogResponse
import com.liber.book_read_management.dto.BookReadLogSearchRequest

public interface BookReadLogQueryRepository {
    fun findByUserIdAndSearchParam(userId: Long, readLogSearchRequest: BookReadLogSearchRequest): List<BookReadLogResponse>
}
