package com.liber.book_read_management.repository

import com.liber.book_read_management.entities.NaverBookImage
import org.springframework.data.jpa.repository.JpaRepository

interface NaverBookImageRepository : JpaRepository<NaverBookImage, Long> {
    fun findByIsbn(isbn: String): NaverBookImage?

    fun findAllByIsbnIn(isbns: Collection<String>): List<NaverBookImage>
}
