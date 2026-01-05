package com.liber.book_read_management.repository

import com.liber.book_read_management.entities.BookReadLog
import com.liber.book_read_management.repository.query.BookReadLogQueryRepository
import org.springframework.data.jpa.repository.JpaRepository

interface BookReadLogRepository : JpaRepository<BookReadLog, Long>, BookReadLogQueryRepository {

    fun findByUserIdAndBookIsbn(userId: Long, bookIsbn: String): BookReadLog?
    fun findByUserIdAndId(userId: Long, id: Long): BookReadLog?
}