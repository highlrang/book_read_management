package com.liber.book_read_management.dto;

import com.liber.book_read_management.annotation.Email
import com.liber.book_read_management.annotation.Password
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "로그인 요청")
class LoginRequest (
    @Email
    @Schema(description = "이메일", example = "test@test.com")
    var email: String,
    @Password
    @Schema(description = "평문 비밀번호. auth.encryption.allow-plain-password=false 이후에는 사용하지 않습니다.", example = "password1234!", nullable = true)
    var password: String? = null,
    @Schema(description = "RSA 공개키로 암호화한 Base64 비밀번호", nullable = true)
    var encryptedPassword: String? = null,
    @Schema(description = "FCM 토큰", example = "d7Ck...token")
    var fcmToken: String? = null
)
