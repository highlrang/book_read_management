package com.liber.book_read_management.util

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ObjectNode
import com.liber.book_read_management.exception.ExceptionType
import mu.KotlinLogging
import net.logstash.logback.argument.StructuredArguments.entries
import org.springframework.stereotype.Component
import org.springframework.web.util.ContentCachingRequestWrapper
import org.springframework.web.util.ContentCachingResponseWrapper

private val log = KotlinLogging.logger {}

@Component
class LogUtil(val objectMapper: ObjectMapper) {
    private companion object {
        private val SENSITIVE_HEADERS = setOf("authorization")
        private val SENSITIVE_FIELDS = setOf(
            "password",
            "encryptedPassword",
            "accessToken",
            "refreshToken",
            "fcmToken",
            "token",
            "authorization"
        )
        private const val MASKED_VALUE = "***"
    }

    // 요청 로그
    fun logRequest(request: ContentCachingRequestWrapper) {
        val method = request.method
        val requestUri = request.requestURI
        val headerNames = request.headerNames
        val headers = LinkedHashMap<String, String>()
        for (headerName in headerNames) {
            headers[headerName] = maskHeaderValue(headerName, request.getHeader(headerName))
        }
        val body = request.contentAsByteArray.toString(Charsets.UTF_8)
        val logMap = LinkedHashMap<String, Any?>()
        logMap["type"] = "REQUEST"
        logMap["method"] = method
        logMap["requestUri"] = requestUri
        logMap["headers"] = headers
        logMap["body"] = sanitizeBody(body)
        log.info("REQUEST {}", entries(logMap))
    }

    // 응답 로그
    fun logResponse(response: ContentCachingResponseWrapper) {
        val status = response.status
        val headerNames = response.headerNames
        val headers = LinkedHashMap<String, String>()
        for (headerName in headerNames) {
            headers[headerName] = maskHeaderValue(headerName, response.getHeader(headerName))
        }

        val body = response.contentAsByteArray.toString(Charsets.UTF_8)
        val logMap = LinkedHashMap<String, Any?>()
        logMap["type"] = "RESPONSE"
        logMap["status"] = status
        logMap["headers"] = headers
        logMap["body"] = sanitizeBody(body)
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

    private fun maskHeaderValue(headerName: String, value: String?): String {
        if (value.isNullOrBlank()) {
            return value ?: ""
        }

        return if (headerName.lowercase() in SENSITIVE_HEADERS) MASKED_VALUE else value
    }

    private fun sanitizeBody(body: String): Any? {
        if (body.isBlank()) {
            return null
        }

        return try {
            sanitizeJsonNode(objectMapper.readTree(body))
        } catch (_: Exception) {
            body
        }
    }

    private fun sanitizeJsonNode(node: com.fasterxml.jackson.databind.JsonNode): com.fasterxml.jackson.databind.JsonNode {
        if (node.isObject) {
            val objectNode = node.deepCopy<ObjectNode>()
            val fieldNames = objectNode.fieldNames().asSequence().toList()
            for (fieldName in fieldNames) {
                if (fieldName in SENSITIVE_FIELDS) {
                    objectNode.put(fieldName, MASKED_VALUE)
                } else {
                    objectNode.set(fieldName, sanitizeJsonNode(objectNode[fieldName]))
                }
            }
            return objectNode
        }

        if (node.isArray) {
            val arrayNode = objectMapper.createArrayNode()
            node.forEach { arrayNode.add(sanitizeJsonNode(it)) }
            return arrayNode
        }

        return node
    }
}
