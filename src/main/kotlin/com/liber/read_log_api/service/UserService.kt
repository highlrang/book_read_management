package com.liber.read_log_api.service

import com.liber.read_log_api.dto.AuthResponse
import com.liber.read_log_api.dto.LoginRequest
import com.liber.read_log_api.dto.SignUpRequest

interface UserService {

    fun signUp(request: SignUpRequest): AuthResponse
    fun login(request: LoginRequest): AuthResponse
    fun logout(userId: Long)
}