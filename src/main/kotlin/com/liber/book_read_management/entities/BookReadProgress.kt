package com.liber.book_read_management.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

/**
 * 언제 어디까지 읽었는지 히스토리로 남기기 위함
 */
@Entity
@Table(name = "BOOK_READ_PROGRESS")
class BookReadProgress (

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = null,

    @Column(name = "user_id")
    var userId : Long,

    @Column(name = "book_read_log_id")
    var bookReadLogId : Long,

    @Column(name = "read_page")
    var readPage: Int,
    @Column(name = "read_diff")
    var readDiff: Int,

) : BaseTimeEntity() {

    companion object {
        fun of(userId: Long, bookReadLogId: Long, readPage: Int, readDiff: Int): BookReadProgress {
            return BookReadProgress(
                userId = userId,
                bookReadLogId = bookReadLogId,
                readPage = readPage,
                readDiff = readDiff
            )
        }
    }
}
