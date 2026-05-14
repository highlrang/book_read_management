package com.liber.book_read_management.dto

import com.liber.book_read_management.enums.SocialProvider
import com.liber.book_read_management.enums.SocialLoginPlatform
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "소셜 로그인 요청")
data class SocialLoginRequest(
    @Schema(description = "소셜 provider", allowableValues = ["GOOGLE", "KAKAO", "NAVER"])
    val provider: SocialProvider,
    @Schema(description = "소셜 토큰")
    val token: String,
    @Schema(description = "소셜 로그인 플랫폼", allowableValues = ["IOS", "AOS"])
    val platform: SocialLoginPlatform? = null
)
