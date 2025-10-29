package com.liber.book_read_management.config

import com.liber.book_read_management.auth.AuthFilter
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class FilterConfig(
    private val authFilter: AuthFilter,
    private val loggingFilter: LoggingFilter
) {
    @Bean
    fun loggingFilterRegistration(): FilterRegistrationBean<LoggingFilter> {
        val registration = FilterRegistrationBean(loggingFilter)
        registration.addUrlPatterns("/*")
        registration.order = 0
        return registration
    }

    @Bean
    fun filterRegistration(): FilterRegistrationBean<AuthFilter> {
        val registration = FilterRegistrationBean(authFilter)
        registration.addUrlPatterns("/*")
        registration.order = 1
        return registration
    }

}