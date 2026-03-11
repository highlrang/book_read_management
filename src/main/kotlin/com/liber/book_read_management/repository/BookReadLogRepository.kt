package com.liber.book_read_management.repository

import com.liber.book_read_management.entities.BookReadLog
import com.liber.book_read_management.enums.BookReadStatus
import com.liber.book_read_management.repository.query.BookReadLogQueryRepository
import org.springframework.data.jpa.repository.JpaRepository

interface BookReadLogRepository : JpaRepository<BookReadLog, Long>, BookReadLogQueryRepository {

    fun findAllByUserId(userId: Long): List<BookReadLog>
    fun findByUserIdAndBookIsbn(userId: Long, bookIsbn: String): BookReadLog?
    fun findByUserIdAndId(userId: Long, id: Long): BookReadLog?
    fun findTopByUserIdOrderByCreatedAtAsc(userId: Long): BookReadLog?
    fun findTop10ByUserIdOrderByIdDesc(userId: Long): List<BookReadLog>
    fun countByUserIdAndReadStatus(userId: Long, readStatus: BookReadStatus): Long
    fun findAllByUserIdAndCategoryGroupNotNull(userId: Long): List<BookReadLog>
}
