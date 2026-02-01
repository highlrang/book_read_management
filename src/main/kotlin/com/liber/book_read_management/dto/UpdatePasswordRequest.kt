package com.liber.book_read_management.dto

import com.liber.book_read_management.annotation.Email
import com.liber.book_read_management.annotation.Password
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "패스워드 변경 요청")
class UpdatePasswordRequest(
    @Email
    @Schema(description = "이메일", example = "test@test.com")
    var email: String,
    @Password
    @Schema(description = "새 비밀번호", example = "new_password1234!")
    var password: String
)
