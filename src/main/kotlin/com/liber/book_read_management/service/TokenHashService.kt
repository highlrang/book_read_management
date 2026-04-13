package com.liber.book_read_management.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.security.MessageDigest

@Service
class TokenHashService(
    @Value("\${security.token-hash.secret:\${SECRET_KEY:}}")
    private val secret: String
) {
    fun hash(token: String?): String? {
        if (token.isNullOrBlank()) {
            return token
        }

        val digest = MessageDigest.getInstance("SHA-256")
        val bytes = digest.digest((secret + token).toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }

    fun matches(rawToken: String?, hashedToken: String?): Boolean {
        if (rawToken.isNullOrBlank() || hashedToken.isNullOrBlank()) {
            return false
        }

        return hash(rawToken) == hashedToken
    }
}
