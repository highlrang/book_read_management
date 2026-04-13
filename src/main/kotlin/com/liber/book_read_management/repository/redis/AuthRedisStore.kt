package com.liber.book_read_management.repository.redis

import com.liber.book_read_management.exception.ApiException
import com.liber.book_read_management.exception.ExceptionType
import org.springframework.stereotype.Repository
import java.security.MessageDigest

@Repository
class AuthRedisStore(
    private val redisTemplateRepository: RedisTemplateRepository
) {

    private final val emailVerifyCodePrefix = "VERIFY_CODE_"
    private final val emailVerifyTokenPrefix = "VERIFY_TOKEN_"
    private final val verifiedEmailPrefix = "VERIFIED_EMAIL_"

    fun setEmailVerifyCode(email: String, code: String) {
        val emailVerifyCodeKey = "${emailVerifyCodePrefix}${email}"
        redisTemplateRepository.setValue(emailVerifyCodeKey, code)
        redisTemplateRepository.expireKey(emailVerifyCodeKey, 600)
    }

    fun getEmailVerifyCode(email: String): String? {
        return redisTemplateRepository.getValue("${emailVerifyCodePrefix}${email}")?.toString()
    }

    fun setEmailVerificationToken(email: String, token: String) {
        val emailVerifyTokenKey = "${emailVerifyTokenPrefix}${hashToken(token)}"
        redisTemplateRepository.setValue(emailVerifyTokenKey, email)
        redisTemplateRepository.expireKey(emailVerifyTokenKey, 600)
    }

    fun consumeEmailByVerificationToken(token: String): String? {
        val emailVerifyTokenKey = "${emailVerifyTokenPrefix}${hashToken(token)}"
        val email = redisTemplateRepository.getValue(emailVerifyTokenKey)?.toString()
        if (email != null) {
            redisTemplateRepository.delKey(emailVerifyTokenKey)
        }
        return email
    }

    fun setVerifiedEmail(email: String) {
        val verifiedEmailKey = "${verifiedEmailPrefix}${email}"
        redisTemplateRepository.setValue(verifiedEmailKey, "")
        redisTemplateRepository.expireKey(verifiedEmailKey, 600)
    }

    fun checkVerifiedEmail(email: String) {
        if (redisTemplateRepository.getValue("${verifiedEmailPrefix}${email}") == null)
            throw ApiException(ExceptionType.VALIDATION_ERROR)
    }

    fun isVerifiedEmail(email: String): Boolean {
        return redisTemplateRepository.getValue("${verifiedEmailPrefix}${email}") != null
    }

    private fun hashToken(token: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(token.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
