package com.liber.book_read_management.dto

data class SocialProviderMismatchResponse(
    val code: String = "SOCIAL_PROVIDER_MISMATCH",
    val registeredProvider: String,
    val message: String
)
