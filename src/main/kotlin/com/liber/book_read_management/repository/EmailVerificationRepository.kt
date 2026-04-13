package com.liber.book_read_management.repository

import com.liber.book_read_management.entities.EmailVerification
import org.springframework.data.jpa.repository.JpaRepository

interface EmailVerificationRepository : JpaRepository<EmailVerification, Long> {
    fun findByEmail(email: String): EmailVerification?
    fun findByTokenHash(tokenHash: String): EmailVerification?
}
