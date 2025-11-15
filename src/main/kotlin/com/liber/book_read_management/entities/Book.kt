package com.liber.book_read_management.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "BOOK")
class Book (

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = null,
    var isbn: String = "",
    var title: String = "",
    var description: String? = "",
    var author: String = "",
    var publisher: String = "",
    @Column(name = "published_date")
    var publishedDate: String = "",

) : BaseTimeEntity()