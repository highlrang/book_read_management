package com.liber.book_read_management.repository

import com.liber.book_read_management.entities.BookReadProgress
import org.springframework.data.jpa.repository.JpaRepository

interface BookReadProgressRepository : JpaRepository<BookReadProgress, Long> {

    fun findByUserIdAndBookReadLogId(userId: Long, bookReadLogId: Long): BookReadProgress?
}