package com.liber.book_read_management.dto

import com.liber.book_read_management.annotation.Email
import com.liber.book_read_management.annotation.Password
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "회원가입 요청")
class SignUpRequest(
    @Email
    @Schema(description = "이메일", example = "test@test.com")
    var email: String,
    @Password
    @Schema(description = "평문 비밀번호. auth.encryption.allow-plain-password=false 이후에는 사용하지 않습니다.", example = "password1234!", nullable = true)
    var password: String? = null,
    @Schema(description = "RSA 공개키로 암호화한 Base64 비밀번호", nullable = true)
    var encryptedPassword: String? = null,
    @NotBlank
    @Size(max=10)
    @Schema(description = "별명", example = "홍길동")
    var nickname: String,
    @Schema(description = "사진", example = "1")
    var photoId: Long?
)