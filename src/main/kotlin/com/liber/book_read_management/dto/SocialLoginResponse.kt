package com.liber.book_read_management.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "소셜 로그인 응답")
data class SocialLoginResponse(
    @Schema(description = "액세스 토큰")
    val accessToken: String,
    @Schema(description = "리프레시 토큰")
    val refreshToken: String
)
