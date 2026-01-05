package com.liber.book_read_management.entities

import com.liber.book_read_management.dto.BookReadLogSaveRequest
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "BOOK_READ_LOG")
class BookReadLog (

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = null,
    @Column(name = "user_id")
    var userId : Long,
    @Column(name = "book_isbn")
    var bookIsbn: String,
    @Column(name = "total_page")
    var totalPage: Int? = null,

) : BaseTimeEntity() {

    companion object {
        fun of(userId: Long, bookReadLogSaveRequest: BookReadLogSaveRequest) : BookReadLog {
            return BookReadLog(
                userId = userId,
                bookIsbn = bookReadLogSaveRequest.bookSbn
            )
        }
    }
}