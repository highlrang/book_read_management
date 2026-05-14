package com.liber.book_read_management.service

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.liber.book_read_management.enums.SocialLoginPlatform
import com.liber.book_read_management.enums.SocialProvider
import com.liber.book_read_management.exception.ApiException
import com.liber.book_read_management.exception.ExceptionType
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import java.math.BigInteger
import java.security.KeyFactory
import java.security.interfaces.RSAPublicKey
import java.security.spec.RSAPublicKeySpec
import java.util.Base64
import java.util.Date

data class SocialProfile(
    val provider: SocialProvider,
    val socialId: String,
    val email: String,
    val name: String?
)

@Service
class SocialProfileService(
    restClientBuilder: RestClient.Builder,
    @Value("\${auth.social.google.aos-client-id:}") private val googleAosClientId: String,
    @Value("\${auth.social.google.ios-client-id:}") private val googleIosClientId: String
) {
    private val restClient = restClientBuilder.build()
    private val keyFactory = KeyFactory.getInstance("RSA")

    fun getProfile(provider: SocialProvider, token: String, platform: SocialLoginPlatform?): SocialProfile {
        if (token.isBlank()) {
            throw ApiException(ExceptionType.INVALID_AUTH)
        }

        return when (provider) {
            SocialProvider.GOOGLE -> runCatching {
                verifyGoogleIdToken(token, platform)
            }.getOrElse {
                if (it is ApiException) throw it
                throw ApiException(ExceptionType.INVALID_AUTH)
            }
            SocialProvider.KAKAO -> getKakaoProfile(token)
            SocialProvider.NAVER -> getNaverProfile(token)
            SocialProvider.LOCAL -> throw ApiException(ExceptionType.VALIDATION_ERROR)
        }
    }

    private fun verifyGoogleIdToken(idToken: String, platform: SocialLoginPlatform?): SocialProfile {
        val googleClientIds = googleClientIdsFor(platform)
        if (googleClientIds.isEmpty()) {
            val platformName = platform?.name ?: "AOS/IOS"
            throw ApiException(ExceptionType.INVALID_AUTH, "Google $platformName client id 설정이 필요합니다.")
        }

        val decoded = JWT.decode(idToken)
        if (decoded.algorithm != "RS256" || decoded.keyId.isNullOrBlank()) {
            throw ApiException(ExceptionType.INVALID_AUTH)
        }

        val jwk = fetchGoogleJwks().keys.firstOrNull { it.kid == decoded.keyId }
            ?: throw ApiException(ExceptionType.INVALID_AUTH)
        val publicKey = jwk.toRsaPublicKey()
        val verified = JWT.require(Algorithm.RSA256(publicKey, null))
            .build()
            .verify(idToken)

        if (verified.issuer !in GOOGLE_ISSUERS) {
            throw ApiException(ExceptionType.INVALID_AUTH)
        }
        if (verified.audience.none { it in googleClientIds }) {
            throw ApiException(ExceptionType.INVALID_AUTH)
        }
        if (verified.expiresAt == null || verified.expiresAt.before(Date())) {
            throw ApiException(ExceptionType.INVALID_AUTH)
        }

        val email = verified.getClaim("email").asString()
        val emailVerified = verified.getClaim("email_verified").asBoolean() == true
        val subject = verified.subject
        if (email.isNullOrBlank() || subject.isNullOrBlank() || !emailVerified) {
            throw ApiException(ExceptionType.INVALID_AUTH)
        }

        return SocialProfile(
            provider = SocialProvider.GOOGLE,
            socialId = subject,
            email = email,
            name = verified.getClaim("name").asString()
        )
    }

    private fun googleClientIdsFor(platform: SocialLoginPlatform?): List<String> {
        return when (platform) {
            SocialLoginPlatform.AOS -> listOf(googleAosClientId)
            SocialLoginPlatform.IOS -> listOf(googleIosClientId)
            null -> listOf(googleAosClientId, googleIosClientId).filter { it.isNotBlank() }
        }
    }

    private fun getKakaoProfile(accessToken: String): SocialProfile {
        val response = runCatching {
            restClient.get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .retrieve()
                .body(KakaoUserResponse::class.java)
        }.getOrElse {
            throw ApiException(ExceptionType.INVALID_AUTH)
        } ?: throw ApiException(ExceptionType.INVALID_AUTH)

        val socialId = response.id?.toString()
        val email = response.kakaoAccount?.email
        if (socialId.isNullOrBlank() || email.isNullOrBlank()) {
            throw ApiException(ExceptionType.INVALID_AUTH)
        }

        return SocialProfile(
            provider = SocialProvider.KAKAO,
            socialId = socialId,
            email = email,
            name = response.kakaoAccount.profile?.nickname ?: response.properties?.nickname
        )
    }

    private fun getNaverProfile(accessToken: String): SocialProfile {
        val response = runCatching {
            restClient.get()
                .uri("https://openapi.naver.com/v1/nid/me")
                .header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")
                .retrieve()
                .body(NaverUserResponse::class.java)
        }.getOrElse {
            throw ApiException(ExceptionType.INVALID_AUTH)
        } ?: throw ApiException(ExceptionType.INVALID_AUTH)

        if (response.resultcode != "00") {
            throw ApiException(ExceptionType.INVALID_AUTH)
        }

        val profile = response.response ?: throw ApiException(ExceptionType.INVALID_AUTH)
        if (profile.id.isNullOrBlank() || profile.email.isNullOrBlank()) {
            throw ApiException(ExceptionType.INVALID_AUTH)
        }

        return SocialProfile(
            provider = SocialProvider.NAVER,
            socialId = profile.id,
            email = profile.email,
            name = profile.name ?: profile.nickname
        )
    }

    private fun fetchGoogleJwks(): GoogleJwkSet {
        return runCatching {
            restClient.get()
                .uri("https://www.googleapis.com/oauth2/v3/certs")
                .retrieve()
                .body(GoogleJwkSet::class.java)
        }.getOrElse {
            throw ApiException(ExceptionType.INVALID_AUTH)
        } ?: throw ApiException(ExceptionType.INVALID_AUTH)
    }

    private fun GoogleJwk.toRsaPublicKey(): RSAPublicKey {
        if (kty != "RSA" || n.isBlank() || e.isBlank()) {
            throw ApiException(ExceptionType.INVALID_AUTH)
        }
        val modulus = BigInteger(1, Base64.getUrlDecoder().decode(n))
        val exponent = BigInteger(1, Base64.getUrlDecoder().decode(e))
        return keyFactory.generatePublic(RSAPublicKeySpec(modulus, exponent)) as RSAPublicKey
    }

    companion object {
        private val GOOGLE_ISSUERS = setOf("accounts.google.com", "https://accounts.google.com")
    }
}

@JsonIgnoreProperties(ignoreUnknown = true)
data class GoogleJwkSet(
    val keys: List<GoogleJwk> = emptyList()
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class GoogleJwk(
    val kid: String = "",
    val kty: String = "",
    val n: String = "",
    val e: String = ""
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class KakaoUserResponse(
    val id: Long? = null,
    val properties: KakaoProperties? = null,
    @JsonProperty("kakao_account")
    val kakaoAccount: KakaoAccount? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class KakaoProperties(
    val nickname: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class KakaoAccount(
    val email: String? = null,
    val profile: KakaoProfile? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class KakaoProfile(
    val nickname: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class NaverUserResponse(
    val resultcode: String? = null,
    val message: String? = null,
    val response: NaverProfile? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class NaverProfile(
    val id: String? = null,
    val email: String? = null,
    val name: String? = null,
    val nickname: String? = null
)
