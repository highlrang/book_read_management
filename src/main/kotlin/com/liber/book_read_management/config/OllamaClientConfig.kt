package com.liber.book_read_management.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient
import java.net.http.HttpClient

@Configuration
class HttpClientConfig(
) {
    @Bean
    fun httpClient(): HttpClient {
        return HttpClient.newHttpClient()
    }
}