package com.liber.book_read_management.controller.view

import com.liber.book_read_management.repository.redis.AuthRedisStore
import com.liber.book_read_management.repository.redis.RedisTemplateRepository
import com.liber.book_read_management.service.UserService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/auth")
class AuthController(private val userService: UserService) {

    @Operation(summary = "메일 인증 코드 검증", description = "메일에 발송된 인증 코드를 검증합니다.")
    @GetMapping("/verify-email")
    fun verifyEmail(@RequestParam email: String, @RequestParam code: String): String {
        return try {
            userService.verifyEmail(email, code)
            "redirect:/email_verified.html"

        } catch (ex: Exception) {
            "redirect:/error.html"
        }
    }
}