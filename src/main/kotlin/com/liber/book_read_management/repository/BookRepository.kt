package com.liber.book_read_management.repository

import com.liber.book_read_management.entities.Book
import org.springframework.data.jpa.repository.JpaRepository

interface BookRepository : JpaRepository<Book, Long> {
    fun findByIsbn(isbn: String): Book?
}
