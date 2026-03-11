package com.liber.book_read_management.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import com.liber.book_read_management.config.ACCESS_TOKEN_EXPIRATION_TIME
import com.liber.book_read_management.config.REFRESH_TOKEN_EXPIRATION_TIME
import com.liber.book_read_management.exception.ApiException
import com.liber.book_read_management.exception.ExceptionType
import com.liber.book_read_management.util.LogUtil
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class TokenUtil(@Value("\${secretKey}") private val secretKey: String,
                val logUtil: LogUtil) {

    private val keyBytes: ByteArray = Base64.getDecoder().decode(secretKey)
    private val algorithm = Algorithm.HMAC256(keyBytes)

    // 추후 휴대폰 인증과 OAuth 인증까지 확장할 수 있도록 분리한다.
    fun createAccessToken(userId: Long): String {
        return JWT.create()
            .withIssuer("my-app")          // 토큰 발급자
            .withSubject(userId.toString())           // 사용자 식별자
            .withClaim("role", "USER")     // 추가 권한 정보
            .withExpiresAt(Date(System.currentTimeMillis() + ACCESS_TOKEN_EXPIRATION_TIME)) // 만료 시간
            .sign(algorithm)
    }

    fun createRefreshToken(userId: Long): String {
        return JWT.create()
            .withIssuer("my-app")          // 토큰 발급자
            .withSubject(userId.toString())           // 사용자 식별자
            .withClaim("role", "USER")     // 추가 권한 정보
            .withExpiresAt(Date(System.currentTimeMillis() + REFRESH_TOKEN_EXPIRATION_TIME)) // 만료 시간
            .sign(algorithm)
    }

    fun verifyToken(token: String): DecodedJWT {
        try {
            val verifier = JWT.require(algorithm)
                .withIssuer("my-app")   // 발급자 검증
                .build()

            val decodedJWT = verifier.verify(token)
            return decodedJWT

        } catch (ex: JWTVerificationException) {
            logUtil.logError(ex)
            throw ApiException(ExceptionType.INVALID_AUTH)
        }
    }

    fun matchToken(authorizationToken: String, lastAccessToken: String) {
        val isUnAuthorized = authorizationToken != lastAccessToken
        if (isUnAuthorized) throw ApiException(ExceptionType.INVALID_AUTH)
    }

    fun getUserId(token: DecodedJWT): Long {
        return token.subject.toLong()
    }

    //fun isValid(token: String): Boolean {
    //}
}
