package com.liber.book_read_management.dto

import com.liber.book_read_management.annotation.Email
import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "이메일 발송 요청")
class SendEmailRequest(
    @Email
    @Schema(description = "이메일", example = "test@test.com")
    val email: String
)