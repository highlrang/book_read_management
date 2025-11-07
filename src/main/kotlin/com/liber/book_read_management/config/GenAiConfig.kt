package com.liber.book_read_management.config

import com.google.genai.Client
import com.google.genai.Models
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class GenAiConfig {

    @Value("\${google.ai.api-key}")
    private var apiKey: String? = null

    @Bean
    fun genAiClient(): Client? {
        return Client.builder().apiKey(apiKey).build();
    }
}