package com.liber.read_log_api.config

import com.liber.read_log_api.util.LogUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
import java.util.*

@Component
class LoggingFilter : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val requestWrapper = ContentCachingRequestWrapper(request)
        val responseWrapper = ContentCachingResponseWrapper(response)

        MDC.put("requestId", UUID.randomUUID().toString())

        filterChain.doFilter(requestWrapper, responseWrapper)

        LogUtil.logRequest(requestWrapper)
        LogUtil.logResponse(responseWrapper)

        responseWrapper.copyBodyToResponse()
        MDC.clear()
    }
}
