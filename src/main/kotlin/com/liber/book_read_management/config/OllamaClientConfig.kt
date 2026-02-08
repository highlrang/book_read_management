package com.liber.book_read_management.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class OllamaClientConfig(
    @Value("\${ollamaDomain}")
    private val ollamaDomain: String
) {

    @Bean
    fun webClient(builder: WebClient.Builder): WebClient {
        return builder.baseUrl(ollamaDomain).build()
    }
}