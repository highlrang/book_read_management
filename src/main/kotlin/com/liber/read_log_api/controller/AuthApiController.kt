package com.liber.read_log_api.controller;

import com.liber.read_log_api.auth.CurrentUserId
import com.liber.read_log_api.dto.*
import com.liber.read_log_api.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthApiController(
    private val userService: UserService
) {

    @PostMapping("/sign-up")
    fun signUp(@RequestBody request: SignUpRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        return ResponseEntity.ok(
            ApiResponse.success(userService.signUp(request))
        )
    }

    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        return ResponseEntity.ok(
            ApiResponse.success(userService.login(request))
        )
    }

    @PostMapping("/logout")
    fun logout(@CurrentUserId userId: Long) : ResponseEntity<ApiResponse<Unit>> {
        userService.logout(userId)
        return ResponseEntity.ok(ApiResponse.success())
    }
}
