package com.liber.book_read_management.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "비밀번호 암호화 요청")
class EncryptPasswordRequest(
    @field:NotBlank
    @Schema(description = "암호화할 평문 비밀번호", example = "password1234!")
    val password: String
)
