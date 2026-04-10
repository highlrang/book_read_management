package com.liber.book_read_management.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "비밀번호 암호화 응답")
class EncryptPasswordResponse(
    @Schema(description = "RSA 공개키 ID", example = "primary")
    val keyId: String,
    @Schema(description = "암호화 알고리즘", example = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding")
    val algorithm: String,
    @Schema(description = "RSA 공개키로 암호화한 Base64 비밀번호")
    val encryptedPassword: String
)
