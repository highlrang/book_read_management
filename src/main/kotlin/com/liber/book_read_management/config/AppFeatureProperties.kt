package com.liber.book_read_management.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.features")
data class AppFeatureProperties(
    val address: Feature = Feature(enabled = false)
) {
    data class Feature(
        val enabled: Boolean = false
    )
}
