package com.liber.book_read_management.entities

import com.liber.book_read_management.dto.BookReadLogSaveRequest
import com.liber.book_read_management.dto.BookReadPageUpdateRequest
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

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

    var progress: Int,

) : BaseTimeEntity() {

    fun updateReadPage(readPage: Int, totalPage: Int) {
        this.readPage = readPage
        this.progress = (readPage.toDouble() / totalPage * 100).toInt()
    }

    companion object {
        fun init(userId: Long, bookReadLogId: Long): BookReadProgress {
            return BookReadProgress(
                userId = userId,
                bookReadLogId = bookReadLogId,
                readPage = 0,
                progress = 0
            )
        }
    }
}