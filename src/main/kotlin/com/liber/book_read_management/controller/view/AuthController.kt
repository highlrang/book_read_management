package com.liber.book_read_management.controller.view

import com.liber.book_read_management.repository.redis.AuthRedisStore
import com.liber.book_read_management.repository.redis.RedisTemplateRepository
import io.swagger.v3.oas.annotations.Operation
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/auth")
class AuthController(private val authRedisStore: AuthRedisStore) {

    @Operation(summary = "인증 메일 발송", description = "인증 메일을 발송합니다.")
    @GetMapping("/verify-email")
    fun verifyEmail(@RequestParam email: String, @RequestParam code: String): String {
        val savedCode = authRedisStore.getValue(email)
        if (savedCode != code) return "redirect:/error.html"
        return "redirect:/email_verified.html"
    }
}