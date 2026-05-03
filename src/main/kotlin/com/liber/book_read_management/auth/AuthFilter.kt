package com.liber.book_read_management.auth

import com.liber.book_read_management.config.API_KEY_HEADER
import com.liber.book_read_management.entities.User
import com.liber.book_read_management.exception.ApiException
import com.liber.book_read_management.exception.ExceptionType
import com.liber.book_read_management.repository.UserRepository
import com.liber.book_read_management.service.TokenHashService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.filter.OncePerRequestFilter

@Component
class AuthFilter(
    private var userRepository: UserRepository,
    private var tokenUtil: TokenUtil,
    private val tokenHashService: TokenHashService
) : OncePerRequestFilter() {

    companion object {
        private const val BEARER_PREFIX = "Bearer "
    }

    @Value("\${apiKey}")
    private lateinit var API_KEY: String

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        // API Key 인증 패스
        if (isApiKeyNotRequiredPath(request.requestURI)) {
            filterChain.doFilter(request, response)
            return
        }

        // API Key 인증
        val apiKey = request.getHeader(API_KEY_HEADER)
        if (apiKey == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid api key")
            return
        }

        if (API_KEY != apiKey) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid api key")
            return
        }

        // 토큰 인증 패스
        if (isTokenNotRequiredPath(request.requestURI)) {
            filterChain.doFilter(request, response)
            return
        }

        // 토큰 인증
        val authorizationHeader = request.getHeader("Authorization")
        if (authorizationHeader.isNullOrBlank()) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token")
            return
        }

        try {
            val token = extractBearerToken(authorizationHeader)
            val decodedJwt = tokenUtil.verifyToken(token)

            val userId: Long = tokenUtil.getUserId(decodedJwt)

            val user: User = userRepository.findById(userId)
                .orElseThrow { throw ApiException(ExceptionType.DATA_NOT_FOUND) }
            val accessTokenHash = user.accessToken ?: throw ApiException(ExceptionType.INVALID_AUTH)
            if (!tokenHashService.matches(token, accessTokenHash)) {
                throw ApiException(ExceptionType.INVALID_AUTH)
            }

            RequestContextHolder.currentRequestAttributes()
                .setAttribute("userId", userId, RequestAttributes.SCOPE_REQUEST)
            MDC.put("userId", userId.toString())

            filterChain.doFilter(request, response)

        } catch (ex : Exception) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token")

        }
    }

    fun isApiKeyNotRequiredPath(requestUri: String) : Boolean {
        return requestUri.startsWith("/swagger-ui")
            || requestUri.startsWith("/v3/api-docs")
            || requestUri.startsWith("/auth/verify-email")
            || requestUri.startsWith("/email_verified.html")
            || requestUri.startsWith("/error.html")
            || requestUri.startsWith("/app/uploads")
            || requestUri == "/"
    }

    fun isTokenNotRequiredPath(requestUri: String) : Boolean {
        return requestUri.startsWith("/api/v1/auth/nickname/check")
                || requestUri.startsWith("/api/v1/auth/public-key")
                || requestUri.startsWith("/api/v1/auth/encrypt-password")
                || requestUri.startsWith("/api/v1/auth/sign-up")
                || requestUri.startsWith("/api/v1/auth/login")
                || requestUri.startsWith("/api/v1/auth/refresh")
                || requestUri.startsWith("/api/v1/auth/send-verification-email")
                || requestUri.startsWith("/api/v1/auth/verify-email")
                || requestUri.startsWith("/api/v1/files")
                || requestUri.startsWith("/app/uploads")
                || requestUri.startsWith("/api/v1/auth/password")
    }

    fun extractBearerToken(authorizationHeader: String): String {
        if (!authorizationHeader.startsWith(BEARER_PREFIX)) {
            throw ApiException(ExceptionType.INVALID_AUTH)
        }

        val token = authorizationHeader.removePrefix(BEARER_PREFIX).trim()
        if (token.isBlank()) {
            throw ApiException(ExceptionType.INVALID_AUTH)
        }

        return token
    }
}
