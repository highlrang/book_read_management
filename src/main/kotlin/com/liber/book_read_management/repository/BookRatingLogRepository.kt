package com.liber.book_read_management.repository

import com.liber.book_read_management.entities.BookRatingLog
import org.springframework.data.jpa.repository.JpaRepository

interface BookRatingLogRepository : JpaRepository<BookRatingLog, Long> {

    fun findByBookReadLogId(bookReadLogId: Long): BookRatingLog?
}