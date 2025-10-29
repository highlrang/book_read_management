package com.liber.book_read_management.service

import com.liber.book_read_management.dto.AuthResponse
import com.liber.book_read_management.dto.LoginRequest
import com.liber.book_read_management.dto.SignUpRequest

interface UserService {

    fun signUp(request: SignUpRequest): AuthResponse
    fun login(request: LoginRequest): AuthResponse
    fun logout(userId: Long)
}