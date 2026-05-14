package com.liber.book_read_management.exception

import com.liber.book_read_management.dto.ApiResponse
import com.liber.book_read_management.dto.SocialProviderMismatchResponse
import com.liber.book_read_management.enums.SocialProvider
import com.liber.book_read_management.util.LogUtil
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler(val logUtil: LogUtil) {

    @ExceptionHandler(ApiException::class)
    fun handleApiException(e: ApiException): ResponseEntity<ApiResponse<Nothing>> {
        logUtil.logError(e.exceptionType, e.customMessage)
        val response = ApiResponse.fail<Nothing>(e.exceptionType.code, e.customMessage ?: e.exceptionType.message)
        return ResponseEntity(response, resolveHttpStatus(e.exceptionType))
    }

    @ExceptionHandler(SocialProviderMismatchException::class)
    fun handleSocialProviderMismatchException(e: SocialProviderMismatchException): ResponseEntity<SocialProviderMismatchResponse> {
        logUtil.logError(e)
        val response = SocialProviderMismatchResponse(
            registeredProvider = e.registeredProvider.name,
            message = providerMismatchMessage(e.registeredProvider)
        )
        return ResponseEntity(response, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(e: HttpMessageNotReadableException): ResponseEntity<ApiResponse<Nothing>> {
        logUtil.logError(e)
        val exceptionType = ExceptionType.VALIDATION_ERROR
        val response = ApiResponse.fail<Nothing>(exceptionType.code, exceptionType.message)
        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ApiResponse<Nothing>> {
        logUtil.logError(e)
        val exceptionType = ExceptionType.INTERNAL_SERVER_ERROR
        val response = ApiResponse.fail<Nothing>(exceptionType.code, exceptionType.message)
        return ResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    private fun resolveHttpStatus(exceptionType: ExceptionType): HttpStatus {
        return when (exceptionType) {
            ExceptionType.EXPIRED_EMAIL_VERIFICATION_TOKEN -> HttpStatus.GONE
            else -> HttpStatus.BAD_REQUEST
        }
    }

    private fun providerMismatchMessage(provider: SocialProvider): String {
        val providerName = when (provider) {
            SocialProvider.GOOGLE -> "구글"
            SocialProvider.KAKAO -> "카카오"
            SocialProvider.NAVER -> "네이버"
            SocialProvider.LOCAL -> "일반"
        }
        return "이미 ${providerName} 계정으로 가입된 이메일입니다."
    }
}
