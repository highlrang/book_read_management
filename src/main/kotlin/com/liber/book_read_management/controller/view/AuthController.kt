package com.liber.book_read_management.controller.view

import com.liber.book_read_management.service.UserService
import io.swagger.v3.oas.annotations.Operation
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
@RequestMapping("/auth")
class AuthController(private val userService: UserService) {

    @Operation(summary = "메일 인증 검증", description = "신규 token 방식과 기존 email/code 방식을 모두 검증합니다.")
    @GetMapping("/verify-email")
    fun verifyEmail(
        @RequestParam(required = false) token: String?,
        @RequestParam(required = false) email: String?,
        @RequestParam(required = false) code: String?
    ): String {
        return try {
            userService.verifyEmail(token, email, code)
            "redirect:/email_verified.html"

        } catch (ex: Exception) {
            "redirect:/error.html"
        }
    }
}
