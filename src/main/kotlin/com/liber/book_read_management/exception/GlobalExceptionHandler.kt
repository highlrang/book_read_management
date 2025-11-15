package com.liber.book_read_management.exception

import com.liber.book_read_management.dto.ApiResponse
import com.liber.book_read_management.util.LogUtil
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(ApiException::class)
    fun handleApiException(e: ApiException): ResponseEntity<ApiResponse<Nothing>> {
        LogUtil.logError(e.exceptionType, e.customMessage)
        val response = ApiResponse.fail<Nothing>(e.exceptionType.code, e.exceptionType.message)
        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ApiResponse<Nothing>> {
        LogUtil.logError(e)
        val exceptionType = ExceptionType.INTERNAL_SERVER_ERROR
        val response = ApiResponse.fail<Nothing>(exceptionType.code, exceptionType.message)
        return ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
