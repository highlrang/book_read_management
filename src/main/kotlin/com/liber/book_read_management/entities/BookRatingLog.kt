package com.liber.book_read_management.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "BOOK_RATING_LOG")
class BookRatingLog(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = null,

    @Column(name = "book_read_log_id")
    var bookReadLogId : Long,

    var rating: Int,

    var content: String,

) : BaseTimeEntity()