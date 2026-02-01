package com.liber.book_read_management.repository.redis

import com.liber.book_read_management.exception.ApiException
import com.liber.book_read_management.exception.ExceptionType
import org.springframework.stereotype.Repository

@Repository
class AuthRedisStore(
    private val redisTemplateRepository: RedisTemplateRepository
) {

    private final val emailVerifyCodePrefix = "VERIFY_CODE_"
    private final val verifiedEmailPrefix = "VERIFIED_EMAIL_"

    fun setEmailVerifyCode(email: String, code: String) {
        val emailVerifyCodeKey = "${emailVerifyCodePrefix}${email}"
        redisTemplateRepository.setValue(emailVerifyCodeKey, code)
        redisTemplateRepository.expireKey(emailVerifyCodeKey, 600)
    }

    fun getEmailVerifyCode(email: String) : String {
        return redisTemplateRepository.getValue("${emailVerifyCodePrefix}${email}").toString()
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
}