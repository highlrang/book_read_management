package com.liber.book_read_management.repository

import com.liber.book_read_management.entities.BookReviewLog
import org.springframework.data.jpa.repository.JpaRepository

interface BookReviewLogRepository : JpaRepository<BookReviewLog, Long> {

    fun findAllByUserIdAndBookReadLogIdOrderByIdDesc(userId: Long, bookReadLogId: Long) : List<BookReviewLog>
    fun findAllByUserIdAndBookReadLogIdInOrderByIdDesc(userId: Long, bookReadLogIds: List<Long>) : List<BookReviewLog>

}
