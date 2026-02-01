package com.liber.book_read_management.repository

import com.liber.book_read_management.entities.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {

    fun findByEmail(email: String) : User?

}