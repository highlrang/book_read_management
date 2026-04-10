package com.liber.book_read_management.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "인증 암호화 공개키 응답")
class AuthPublicKeyResponse(
    @Schema(description = "RSA 공개키 ID", example = "primary")
    val keyId: String,
    @Schema(description = "암호화 알고리즘", example = "RSA/ECB/OAEPWithSHA-256AndMGF1Padding")
    val algorithm: String,
    @Schema(description = "PEM 형식 RSA 공개키")
    val publicKey: String
)
