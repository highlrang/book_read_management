package com.liber.book_read_management.dto;

import com.liber.book_read_management.annotation.LoginId
import com.liber.book_read_management.annotation.Password
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "로그인 요청")
class LoginRequest (
    @LoginId
    @Schema(description = "로그인 아이디", example = "testuser")
    var loginId: String,
    @Password
    @Schema(description = "비밀번호", example = "password1234!")
    var password: String
)