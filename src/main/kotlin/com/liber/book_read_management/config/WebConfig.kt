package com.liber.book_read_management.config;

import com.liber.book_read_management.auth.CurrentUserIdResolver;
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
        private val currentUserIdResolver:CurrentUserIdResolver
) : WebMvcConfigurer {

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(currentUserIdResolver)
    }

    @Bean
    fun bcryptEncoder() : BCryptPasswordEncoder {
        return BCryptPasswordEncoder()
    }

}
