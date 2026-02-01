package com.liber.book_read_management.repository

import com.liber.book_read_management.entities.File
import org.springframework.data.jpa.repository.JpaRepository

interface FileRepository : JpaRepository<File, Long> {
}
