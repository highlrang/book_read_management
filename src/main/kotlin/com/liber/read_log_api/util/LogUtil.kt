package com.liber.read_log_api.util

import mu.KotlinLogging
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
private val log = KotlinLogging.logger {}

// TODO ContentCachingRequestWrapper
class LogUtil {

    companion object {

        // REQUEST
        fun logRequest(request: ContentCachingRequestWrapper) {
            val method = request.method
            val requestUri = request.requestURI
            val headerNames = request.headerNames
            val headers = LinkedHashMap<String, String>()
            for (headerName in headerNames) {
                headers[headerName] = request.getHeader(headerName)
            }
            val body = request.contentAsByteArray.toString(Charsets.UTF_8)

            log.info("""
                [REQUEST] $method $requestUri
                    Header: $headers
                    Body: $body
            """)
        }

        // RESPONSE
        fun logResponse(response: ContentCachingResponseWrapper) {
            val status = response.status
            val headerNames = response.headerNames
            val headers = LinkedHashMap<String, String>()
            for (headerName in headerNames) {
                headers[headerName] = response.getHeader(headerName) ?: ""
            }

            val body = response.contentAsByteArray.toString(Charsets.UTF_8)
            log.info("""
                [RESPONSE] Status: $status
                    Header: $headers
                    Body: $body
                """)
        }

        // ERROR with Exception
        fun logError(ex: Exception) {
            log.error("[EXCEPTION] $ex")
            val stackTrace = ex.stackTrace
            if (stackTrace.isNotEmpty()) {
                val firstStackTrace = stackTrace[0]
                log.error("\t$firstStackTrace")
            }
        }

        // ERROR with Custom message
        fun logError(message: String) {
            log.error("[EXCEPTION] $message")
        }
    }
}