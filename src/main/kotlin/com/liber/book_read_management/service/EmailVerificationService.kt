package com.liber.book_read_management.service

import com.liber.book_read_management.entities.EmailVerification
import com.liber.book_read_management.exception.ApiException
import com.liber.book_read_management.exception.ExceptionType
import com.liber.book_read_management.repository.EmailVerificationRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class EmailVerificationService(
    private val emailVerificationRepository: EmailVerificationRepository,
    private val tokenHashService: TokenHashService
) {
    @Transactional
    fun issueToken(email: String, token: String, ttlSeconds: Long = 600): String {
        val now = LocalDateTime.now()
        val verification = emailVerificationRepository.findByEmail(email) ?: EmailVerification(email = email)
        verification.tokenHash = tokenHashService.hash(token)
        verification.expiresAt = now.plusSeconds(ttlSeconds)
        verification.verifiedAt = null
        emailVerificationRepository.save(verification)
        return token
    }

    @Transactional
    fun verifyToken(token: String) {
        val tokenHash = tokenHashService.hash(token) ?: throw ApiException(ExceptionType.VALIDATION_ERROR)
        val verification = emailVerificationRepository.findByTokenHash(tokenHash)
            ?: throw ApiException(ExceptionType.VALIDATION_ERROR)

        if (verification.expiresAt.isBefore(LocalDateTime.now())) {
            throw ApiException(ExceptionType.VALIDATION_ERROR)
        }

        verification.verifiedAt = LocalDateTime.now()
        verification.tokenHash = null
    }

    @Transactional(readOnly = true)
    fun ensureVerified(email: String) {
        val verification = emailVerificationRepository.findByEmail(email)
            ?: throw ApiException(ExceptionType.VALIDATION_ERROR)

        if (verification.verifiedAt == null || verification.expiresAt.isBefore(LocalDateTime.now())) {
            throw ApiException(ExceptionType.VALIDATION_ERROR)
        }
    }

    @Transactional(readOnly = true)
    fun isVerified(email: String): Boolean {
        val verification = emailVerificationRepository.findByEmail(email) ?: return false
        return verification.verifiedAt != null && !verification.expiresAt.isBefore(LocalDateTime.now())
    }
}
