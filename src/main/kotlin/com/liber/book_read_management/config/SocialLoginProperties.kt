package com.liber.book_read_management.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "auth.social")
data class SocialLoginProperties(
    val kakao: Provider = Provider(enabled = false)
) {
    data class Provider(
        val enabled: Boolean = true
    )
}
