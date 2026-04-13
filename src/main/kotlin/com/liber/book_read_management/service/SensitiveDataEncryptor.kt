package com.liber.book_read_management.service

import jakarta.annotation.PostConstruct
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.nio.ByteBuffer
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

private val sensitiveDataLog = KotlinLogging.logger {}

@Component
class SensitiveDataEncryptor(
    @Value("\${security.sensitive-data.secret:\${SECRET_KEY:}}")
    private val configuredSecret: String
) {

    @PostConstruct
    fun initialize() {
        delegate = if (configuredSecret.isBlank()) {
            sensitiveDataLog.warn { "Sensitive data encryption secret is blank. Sensitive fields will be stored as plain text." }
            NoOpDelegate
        } else {
            AesGcmDelegate(configuredSecret)
        }
    }

    companion object {
        private const val ENCRYPTED_PREFIX = "enc:v1:"

        @Volatile
        private var delegate: Delegate = NoOpDelegate

        fun encrypt(value: String?): String? = delegate.encrypt(value)

        fun decrypt(value: String?): String? = delegate.decrypt(value)
    }

    private interface Delegate {
        fun encrypt(value: String?): String?
        fun decrypt(value: String?): String?
    }

    private object NoOpDelegate : Delegate {
        override fun encrypt(value: String?): String? = value
        override fun decrypt(value: String?): String? = value
    }

    private class AesGcmDelegate(secret: String) : Delegate {
        private val secureRandom = SecureRandom()
        private val secretKey = SecretKeySpec(
            MessageDigest.getInstance("SHA-256").digest(secret.toByteArray(Charsets.UTF_8)),
            "AES"
        )

        override fun encrypt(value: String?): String? {
            if (value.isNullOrBlank()) {
                return value
            }

            if (value.startsWith(ENCRYPTED_PREFIX)) {
                return value
            }

            val iv = ByteArray(12).also(secureRandom::nextBytes)
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, GCMParameterSpec(128, iv))
            val encrypted = cipher.doFinal(value.toByteArray(Charsets.UTF_8))
            val payload = ByteBuffer.allocate(iv.size + encrypted.size)
                .put(iv)
                .put(encrypted)
                .array()

            return ENCRYPTED_PREFIX + Base64.getUrlEncoder().withoutPadding().encodeToString(payload)
        }

        override fun decrypt(value: String?): String? {
            if (value.isNullOrBlank()) {
                return value
            }

            if (!value.startsWith(ENCRYPTED_PREFIX)) {
                return value
            }

            val payload = Base64.getUrlDecoder().decode(value.removePrefix(ENCRYPTED_PREFIX))
            val iv = payload.copyOfRange(0, 12)
            val cipherText = payload.copyOfRange(12, payload.size)
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(128, iv))

            return String(cipher.doFinal(cipherText), Charsets.UTF_8)
        }
    }
}
