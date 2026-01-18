package com.liber.book_read_management.client

import feign.Logger
import feign.RequestInterceptor
import feign.RequestTemplate
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean

class AladinClientConfig : RequestInterceptor {

    @Value("\${client.aladin.ttbKey}")
    private lateinit var ttbKey : String;

    override fun apply(template: RequestTemplate?) {
        template?.query("ttbkey", ttbKey)
    }

    @Bean
    fun feignLoggerLevel(): Logger.Level {
        return Logger.Level.FULL
    }
}