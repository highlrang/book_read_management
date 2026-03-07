package com.liber.book_read_management.service

import com.liber.book_read_management.dto.*

interface UserService {

    fun signUp(request: SignUpRequest): AuthResponse
    fun login(request: LoginRequest): AuthResponse
    fun logout(userId: Long)
    fun refreshToken(refreshToken: String): AuthResponse
    fun updatePassword(request: UpdatePasswordRequest)
    fun verifyEmail(email: String, code: String)
    fun checkNickname(nickname: String) : NicknameCheckResponse
    fun getReadingGoal(userId: Long): ReadingGoalResponse
    fun upsertReadingGoal(userId: Long, request: ReadingGoalRequest): ReadingGoalResponse

}
