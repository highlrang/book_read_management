package com.liber.book_read_management.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.liber.book_read_management.exception.ExceptionType
import mu.KotlinLogging
import net.logstash.logback.argument.StructuredArguments.entries
import org.springframework.stereotype.Component
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper
private val log = KotlinLogging.logger {}

@Component
class LogUtil(val objectMapper: ObjectMapper) {

    // 요청 로그
    fun logRequest(request: ContentCachingRequestWrapper) {
        val method = request.method
        val requestUri = request.requestURI
        val headerNames = request.headerNames
        val headers = LinkedHashMap<String, String>()
        for (headerName in headerNames) {
            headers[headerName] = request.getHeader(headerName)
        }
        val body = request.contentAsByteArray.toString(Charsets.UTF_8)
        val logMap = LinkedHashMap<String, Any?>()
        logMap["type"] = "REQUEST"
        logMap["method"] = method
        logMap["requestUri"] = requestUri
        logMap["headers"] = headers
        logMap["body"] = objectMapper.readTree(body)
        log.info("REQUEST {}", entries(logMap))
    }

    // 응답 로그
    fun logResponse(response: ContentCachingResponseWrapper) {
        val status = response.status
        val headerNames = response.headerNames
        val headers = LinkedHashMap<String, String>()
        for (headerName in headerNames) {
            headers[headerName] = response.getHeader(headerName) ?: ""
        }

        val body = response.contentAsByteArray.toString(Charsets.UTF_8)
        val logMap = LinkedHashMap<String, Any?>()
        logMap["type"] = "RESPONSE"
        logMap["status"] = status
        logMap["headers"] = headers
        logMap["body"] = objectMapper.readTree(body)
        log.info("RESPONSE {}", entries(logMap))
    }

    // 예외 객체 기반 오류 로그
    fun logError(ex: Exception) {
        val logMap = LinkedHashMap<String, Any?>()
        logMap["type"] = "EXCEPTION"
        logMap["exceptionClass"] = ex::class.qualifiedName
        logMap["message"] = ex.message
        log.error("EXCEPTION {}", entries(logMap), ex)
    }

    // 사용자 정의 메시지 기반 오류 로그
    fun logError(exceptionType: ExceptionType, customMessage: String?) {
        var message = exceptionType.message
        if (customMessage != null) message += "    $customMessage"
        val logMap = LinkedHashMap<String, Any?>()
        logMap["type"] = "EXCEPTION"
        logMap["exceptionType"] = exceptionType.name
        logMap["code"] = exceptionType.code
        logMap["message"] = message
        log.error("EXCEPTION {}", entries(logMap))
    }
}
