package com.liber.book_read_management.repository.query;

import com.liber.book_read_management.dto.BookReadLogResponse
import com.liber.book_read_management.dto.BookReadLogSearchRequest
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable

public interface BookReadLogQueryRepository {
    fun findByUserIdAndSearchParam(userId: Long, readLogSearchRequest: BookReadLogSearchRequest, pageable: Pageable): Page<BookReadLogResponse>
}
