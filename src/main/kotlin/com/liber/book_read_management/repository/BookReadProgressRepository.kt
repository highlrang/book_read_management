package com.liber.book_read_management.repository

import com.liber.book_read_management.entities.BookReadProgress
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime

interface BookReadProgressRepository : JpaRepository<BookReadProgress, Long> {

    fun findTopByUserIdAndBookReadLogIdOrderByIdDesc(userId: Long, bookReadLogId: Long): BookReadProgress?

    fun findAllByUserIdAndBookReadLogIdOrderByIdDesc(userId: Long, bookReadLogId: Long) : List<BookReadProgress>

    fun findAllByUserIdAndCreatedAtLessThanEqualOrderByBookReadLogIdAscIdAsc(
        userId: Long,
        createdAt: LocalDateTime
    ): List<BookReadProgress>
}
