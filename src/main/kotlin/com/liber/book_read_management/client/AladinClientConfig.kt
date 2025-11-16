package com.liber.book_read_management.client

import feign.RequestInterceptor
import feign.RequestTemplate
import org.springframework.beans.factory.annotation.Value
class AladinClientConfig : RequestInterceptor {

    @Value("\${client.aladin.ttbkey}")
    private lateinit var ttbKey : String;

    override fun apply(template: RequestTemplate?) {
        template?.query("ttbkey", ttbKey)
    }
}