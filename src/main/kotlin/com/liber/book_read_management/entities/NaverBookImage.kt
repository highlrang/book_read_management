package com.liber.book_read_management.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "NAVER_BOOK_IMAGE")
class NaverBookImage(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "isbn", nullable = false, unique = true, length = 13)
    var isbn: String,

    @Column(name = "image_url", length = 1000)
    var imageUrl: String? = null
) : BaseTimeEntity()
