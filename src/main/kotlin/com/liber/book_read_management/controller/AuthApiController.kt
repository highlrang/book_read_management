package com.liber.book_read_management.controller;

import com.liber.book_read_management.auth.CurrentUserId
import com.liber.book_read_management.dto.*
import com.liber.book_read_management.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
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
