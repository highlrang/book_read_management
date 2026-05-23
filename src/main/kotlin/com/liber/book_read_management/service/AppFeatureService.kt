package com.liber.book_read_management.service

import com.liber.book_read_management.config.AppFeatureProperties
import com.liber.book_read_management.config.SocialLoginProperties
import com.liber.book_read_management.dto.AppFeatureStatusResponse
import com.liber.book_read_management.enums.SocialProvider
import com.liber.book_read_management.exception.ApiException
import com.liber.book_read_management.exception.ExceptionType
import org.springframework.stereotype.Service

@Service
class AppFeatureService(
    private val socialLoginProperties: SocialLoginProperties,
    private val appFeatureProperties: AppFeatureProperties
) {
    fun getStatus(): AppFeatureStatusResponse {
        return AppFeatureStatusResponse(
            location = appFeatureProperties.address.enabled,
            kakaoLogin = socialLoginProperties.kakao.enabled
        )
    }

    fun validateSocialLoginEnabled(provider: SocialProvider) {
        if (provider == SocialProvider.KAKAO && !socialLoginProperties.kakao.enabled) {
            throw ApiException(ExceptionType.SOCIAL_LOGIN_DISABLED)
        }
    }
}
