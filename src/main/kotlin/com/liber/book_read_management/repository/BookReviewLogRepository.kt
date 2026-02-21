package com.liber.book_read_management.repository

import com.liber.book_read_management.entities.Book
import com.liber.book_read_management.entities.BookReadLog
import com.liber.book_read_management.entities.BookReviewLog
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface BookReviewLogRepository : JpaRepository<BookReviewLog, Long> {

    fun findAllByUserIdAndBookReadLogIdOrderByIdDesc(userId: Long, bookReadLogId: Long) : List<BookReviewLog>

}