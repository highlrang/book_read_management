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
    @Schema(description = "비밀번호", example = "password1234!")
    var password: String,
    @NotBlank
    @Size(max=10)
    @Schema(description = "별명", example = "홍길동")
    var nickname: String,
    @Schema(description = "사진", example = "1")
    var photoId: Long?
)