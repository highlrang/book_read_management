package com.liber.read_log_api.auth

import com.liber.read_log_api.entities.User
import com.liber.read_log_api.repository.UserRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.filter.OncePerRequestFilter

// TODO API Key
@Component
class AuthFilter(
    private var userRepository: UserRepository
) : OncePerRequestFilter() {

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
        // TODO 상수화
        val apiKey = request.getHeader("API_Key")
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
        var token = request.getHeader("Authorization")
        if (token == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token")
            return
        }

        try {
            token = token.substring("Bearer ".length)
            val decodedJwt = TokenUtil.verifyToken(token)

            val userId: Long = TokenUtil.getUserId(decodedJwt)

            val user: User = userRepository.findById(userId)
                .orElseThrow { throw IllegalArgumentException() }
            TokenUtil.matchToken(token, user.accessToken!!) // TODO !!랑 requireNotNull 응답 차이 확인

            RequestContextHolder.currentRequestAttributes()
                .setAttribute("userId", userId, RequestAttributes.SCOPE_REQUEST)

            filterChain.doFilter(request, response)

            /*
            TODO Logging
            val cachingRequest = ContentCachingRequestWrapper(request)
            val cachingResponse = ContentCachingResponseWrapper(response)

            filterChain.doFilter(cachingRequest, cachingResponse)
             */


        } catch (ex : Exception) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token")

        }
    }

    fun isApiKeyNotRequiredPath(requestUri: String) : Boolean {
        return requestUri.startsWith("/swagger-ui")
            || requestUri.startsWith("/v3/api-docs")
    }

    fun isTokenNotRequiredPath(requestUri: String) : Boolean {
        return requestUri.startsWith("/api/v1/auth/sign-up")
                || requestUri.startsWith("/api/v1/auth/login")
    }
}