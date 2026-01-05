package com.liber.book_read_management.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import com.liber.book_read_management.exception.ApiException
import com.liber.book_read_management.exception.ExceptionType
import org.springframework.beans.factory.annotation.Value
import java.util.*

class TokenUtil {

    companion object {

        @Value("\${secretKey}")
        private lateinit var SECRET_KEY: String

        const val accessExpTime = 9999999L

        fun createToken(userId: Long): String {
            val algorithm = Algorithm.HMAC256(SECRET_KEY)
            return JWT.create()
                .withIssuer("my-app")          // 토큰 발급자(iss)
                .withSubject(userId.toString())           // 사용자 식별자(sub)
                .withClaim("role", "USER")     // 커스텀 클레임
                .withExpiresAt(Date(System.currentTimeMillis() + accessExpTime)) // 만료 시간 (1시간)
                .sign(algorithm)
        }

        fun verifyToken(token: String): DecodedJWT {
            try {
                println("TOKEN = " + token)
                val algorithm = Algorithm.HMAC256(SECRET_KEY)
                val verifier = JWT.require(algorithm)
                    .withIssuer("my-app")   // 발급자 확인 (선택)
                    .build()

                val decodedJWT = verifier.verify(token)
                println("User ID: ${decodedJWT.subject}")   // sub 값 꺼내기
                println("Role: ${decodedJWT.getClaim("role").asString()}") // 커스텀 클레임 꺼내기
                return decodedJWT

            } catch (ex: JWTVerificationException) {
                println("Invalid token: ${ex.message}")
                // TODO throw
                throw IllegalArgumentException()
            }
        }

        // TODO Exception
        fun matchToken(authorizationToken: String, lastAccessToken: String) {
            val isUnAuthorized = authorizationToken != lastAccessToken
            if (isUnAuthorized) throw ApiException(ExceptionType.INVALID_AUTH)
        }

        // TODO
        fun getUserId(token: DecodedJWT): Long {
            return token.subject.toLong()
        }
    }
}

// 외부로 빼기
//fun isValid(token: String): Boolean {
//}