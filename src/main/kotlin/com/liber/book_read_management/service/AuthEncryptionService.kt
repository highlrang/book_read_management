package com.liber.book_read_management.service

import com.liber.book_read_management.dto.AuthPublicKeyResponse
import com.liber.book_read_management.dto.EncryptPasswordResponse
import com.liber.book_read_management.exception.ApiException
import com.liber.book_read_management.exception.ExceptionType
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.security.KeyFactory
import java.security.Key
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.MGF1ParameterSpec
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource

private val log = KotlinLogging.logger {}

@Service
class AuthEncryptionService(
    @Value("\${auth.encryption.key-id:primary}")
    keyIdValue: String,
    @Value("\${auth.encryption.algorithm:RSA/ECB/OAEPWithSHA-256AndMGF1Padding}")
    algorithmValue: String,
    @Value("\${auth.encryption.public-key:}")
    publicKeyPem: String,
    @Value("\${auth.encryption.private-key:}")
    privateKeyPem: String,
    @Value("\${auth.encryption.allow-plain-password:}")
    allowPlainPassword: String
) {
    private companion object {
        const val DEFAULT_KEY_ID = "primary"
        const val DEFAULT_ALGORITHM = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding"
    }

    private val keyId = keyIdValue.ifBlank { DEFAULT_KEY_ID }
    private val algorithm = algorithmValue.ifBlank { DEFAULT_ALGORITHM }
    private val publicKeyPem = normalizePem(publicKeyPem)
    private val publicKey: PublicKey? = parsePublicKey(this.publicKeyPem)
    private val privateKey: PrivateKey? = parsePrivateKey(normalizePem(privateKeyPem))
    private val allowPlainPassword: Boolean = parseAllowPlainPassword(allowPlainPassword)

    fun getPublicKey(): AuthPublicKeyResponse {
        val encryptKey = publicKey
            ?: throw ApiException(ExceptionType.VALIDATION_ERROR, "인증 공개키가 아직 설정되지 않았습니다.")

        return AuthPublicKeyResponse(
            keyId = keyId,
            algorithm = algorithm,
            publicKey = formatPem("PUBLIC KEY", encryptKey.encoded)
        )
    }

    fun encryptPassword(password: String): EncryptPasswordResponse {
        val encryptKey = publicKey
            ?: throw ApiException(ExceptionType.VALIDATION_ERROR, "인증 공개키가 아직 설정되지 않았습니다.")

        if (password.isBlank()) {
            throw ApiException(ExceptionType.VALIDATION_ERROR, "암호화할 비밀번호가 비어 있습니다.")
        }

        return try {
            val cipher = initCipher(Cipher.ENCRYPT_MODE, encryptKey)
            val encrypted = cipher.doFinal(password.toByteArray(Charsets.UTF_8))
            EncryptPasswordResponse(
                keyId = keyId,
                algorithm = algorithm,
                encryptedPassword = Base64.getEncoder().encodeToString(encrypted)
            )
        } catch (e: Exception) {
            log.error(e) { "Password encryption failed. keyId=$keyId, algorithm=$algorithm" }
            throw ApiException(ExceptionType.VALIDATION_ERROR, "비밀번호 암호화에 실패했습니다.")
        }
    }

    fun resolvePassword(password: String?, encryptedPassword: String?): String {
        if (!encryptedPassword.isNullOrBlank()) {
            return decryptPassword(encryptedPassword)
        }

        if (!password.isNullOrBlank() && allowPlainPassword) {
            return password
        }

        throw ApiException(
            ExceptionType.VALIDATION_ERROR,
            if (allowPlainPassword) "비밀번호가 누락되었습니다." else "암호화된 비밀번호가 필요합니다."
        )
    }

    fun isConfigured(): Boolean = publicKey != null && privateKey != null

    private fun decryptPassword(encryptedPassword: String): String {
        val decryptKey = privateKey
            ?: throw ApiException(ExceptionType.VALIDATION_ERROR, "인증 복호화 키가 설정되지 않았습니다.")

        return try {
            val cipher = initCipher(Cipher.DECRYPT_MODE, decryptKey)
            val decrypted = cipher.doFinal(Base64.getDecoder().decode(encryptedPassword))
            String(decrypted, Charsets.UTF_8)
        } catch (e: Exception) {
            log.error(e) { "Password decryption failed. keyId=$keyId, algorithm=$algorithm" }
            throw ApiException(ExceptionType.VALIDATION_ERROR, "비밀번호 복호화에 실패했습니다.")
        }
    }

    private fun initCipher(mode: Int, key: Key): Cipher {
        if (algorithm.equals("RSA/ECB/OAEPWithSHA-256AndMGF1Padding", ignoreCase = true)) {
            val cipher = Cipher.getInstance("RSA/ECB/OAEPPadding")
            val oaepSpec = OAEPParameterSpec(
                "SHA-256",
                "MGF1",
                MGF1ParameterSpec.SHA256,
                PSource.PSpecified.DEFAULT
            )
            cipher.init(mode, key, oaepSpec)
            return cipher
        }

        return Cipher.getInstance(algorithm).apply {
            init(mode, key)
        }
    }

    private fun parsePublicKey(pem: String): PublicKey? {
        if (pem.isBlank()) {
            return null
        }

        val keyBytes = decodePem(pem, "PUBLIC KEY")
        val spec = X509EncodedKeySpec(keyBytes)
        return KeyFactory.getInstance("RSA").generatePublic(spec)
    }

    private fun parsePrivateKey(pem: String): PrivateKey? {
        if (pem.isBlank()) {
            return null
        }

        val keyBytes = decodePem(pem, "PRIVATE KEY")
        val spec = PKCS8EncodedKeySpec(keyBytes)
        return KeyFactory.getInstance("RSA").generatePrivate(spec)
    }

    private fun normalizePem(value: String): String = value.trim().replace("\\n", "\n")

    private fun parseAllowPlainPassword(value: String): Boolean {
        return when (value.trim().lowercase()) {
            "", "true" -> true
            "false" -> false
            else -> throw ApiException(
                ExceptionType.VALIDATION_ERROR,
                "auth.encryption.allow-plain-password 설정값은 true 또는 false 여야 합니다."
            )
        }
    }

    private fun decodePem(pem: String, keyType: String): ByteArray {
        val normalized = pem
            .replace("-----BEGIN $keyType-----", "")
            .replace("-----END $keyType-----", "")
            .replace("\\s".toRegex(), "")

        return Base64.getDecoder().decode(normalized)
    }

    private fun formatPem(keyType: String, encodedKey: ByteArray): String {
        val body = Base64.getMimeEncoder(64, "\n".toByteArray()).encodeToString(encodedKey)
        return "-----BEGIN $keyType-----\n$body\n-----END $keyType-----"
    }
}
