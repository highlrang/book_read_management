package com.liber.book_read_management.dto

import com.liber.book_read_management.annotation.LoginId
import com.liber.book_read_management.annotation.Password
import com.liber.book_read_management.annotation.PhoneNumber
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

@Schema(description = "회원가입 요청")
class SignUpRequest(
    @LoginId
    @Schema(description = "로그인 아이디", example = "testuser")
    var loginId: String,
    @Password
    @Schema(description = "비밀번호", example = "password1234!")
    var password: String,
    @NotBlank
    @Size(max=10)
    @Schema(description = "이름", example = "홍길동")
    var name: String,
    @PhoneNumber
    @Schema(description = "전화번호", example = "010-1234-5678")
    var phoneNumber: String
)