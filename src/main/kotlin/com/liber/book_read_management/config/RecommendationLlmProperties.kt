package com.liber.book_read_management.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "recommendation.llm")
class RecommendationLlmProperties {
    var plannerModel: String = "gemini-2.5-flash-lite"
    var explainerModel: String = "gemini-2.5-flash"
}
