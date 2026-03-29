package com.liber.book_read_management.client

import feign.Logger
import feign.RequestInterceptor
import feign.RequestTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean

class NaverSearchClientConfig : RequestInterceptor {

    @Value("\${client.naver.clientId:}")
    private lateinit var clientId: String

    @Value("\${client.naver.clientSecret:}")
    private lateinit var clientSecret: String

    override fun apply(template: RequestTemplate?) {
        if (clientId.isNotBlank()) {
            template?.header("X-Naver-Client-Id", clientId)
        }
        if (clientSecret.isNotBlank()) {
            template?.header("X-Naver-Client-Secret", clientSecret)
        }
    }

    @Bean
    fun feignLoggerLevel(): Logger.Level {
        return Logger.Level.BASIC
    }
}
