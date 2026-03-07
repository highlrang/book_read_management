package com.liber.book_read_management.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import com.liber.book_read_management.enums.CategoryGroup

@Entity
@Table(name = "BOOK")
class Book (

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = null,
    var isbn: String = "",
    var title: String = "",
    var description: String? = "",
    var author: String = "",
    @Column(name = "cover")
    var cover: String? = null,
    var publisher: String = "",
    @Column(name = "published_date")
    var publishedDate: String = "",
    @Column(name = "total_page")
    var totalPage: Int? = null,
    @Column(name = "category_path")
    var categoryPath: String? = null,
    @Enumerated(EnumType.STRING)
    @Column(name = "category_group")
    var categoryGroup: CategoryGroup? = null,

) : BaseTimeEntity()
