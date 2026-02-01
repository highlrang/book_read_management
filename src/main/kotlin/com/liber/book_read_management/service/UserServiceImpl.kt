package com.liber.book_read_management.service

import com.liber.book_read_management.auth.TokenUtil
import com.liber.book_read_management.dto.AuthResponse
import com.liber.book_read_management.dto.LoginRequest
import com.liber.book_read_management.dto.SignUpRequest
import com.liber.book_read_management.dto.UpdatePasswordRequest
import com.liber.book_read_management.entities.User
import com.liber.book_read_management.exception.ApiException
import com.liber.book_read_management.exception.ExceptionType
import com.liber.book_read_management.repository.UserRepository
import com.liber.book_read_management.repository.redis.AuthRedisStore
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserServiceImpl(
    private var userRepository: UserRepository,
    private var bcryptEncoder: BCryptPasswordEncoder,
    private var tokenUtil: TokenUtil,
    private var authRedisStore: AuthRedisStore
) : UserService {

    @Transactional
    override fun signUp(request: SignUpRequest): AuthResponse {
        val existUser = userRepository.findByEmail(request.email)
        if (existUser != null) {
            throw ApiException(ExceptionType.ALREADY_EXIST)
        }

        authRedisStore.checkVerifiedEmail(request.email)

        val user = userRepository.save(User(
            email = request.email,
            password = encryptPassword(request.password),
            nickname = request.nickname,
            photoId = request.photoId
        ))
        val userId = user.id!!
        val accessToken = tokenUtil.createAccessToken(userId)
        val refreshToken = tokenUtil.createRefreshToken(userId)
        user.accessToken = accessToken
        user.refreshToken = refreshToken

        return AuthResponse(userId, accessToken, refreshToken)
    }

    @Transactional
    override fun login(request: LoginRequest) : AuthResponse {
        val user = userRepository.findByEmail(request.email) ?:
            throw ApiException(ExceptionType.DATA_NOT_FOUND)

        if (!bcryptEncoder.matches(request.password, user.password))
            throw ApiException(ExceptionType.PASSWORD_NOT_MATCHED)

        val userId = user.id!!
        val accessToken = tokenUtil.createAccessToken(userId)
        val refreshToken = tokenUtil.createRefreshToken(userId)

        user.accessToken = accessToken
        user.refreshToken = refreshToken

        return AuthResponse(userId, accessToken, refreshToken)
    }

    @Transactional
    override fun logout(userId: Long) {
        val user : User = userRepository.findById(userId)
            .orElseThrow { throw ApiException(ExceptionType.DATA_NOT_FOUND) }

        user.accessToken = null
    }

    override fun refreshToken(refreshToken: String): AuthResponse {
        val decodedJWT = tokenUtil.verifyToken(refreshToken)
        val userId = decodedJWT.subject.toLong()
        val user = userRepository.findById(userId)
            .orElseThrow { throw ApiException(ExceptionType.DATA_NOT_FOUND) }

        val newAccessToken = tokenUtil.createAccessToken(user.id!!)
        val newRefreshToken = tokenUtil.createRefreshToken(user.id!!)

        user.accessToken = newAccessToken
        user.refreshToken = newRefreshToken

        return AuthResponse(user.id!!, newAccessToken, newRefreshToken)
    }

    @Transactional
    override fun updatePassword(request: UpdatePasswordRequest) {
        val user = userRepository.findByEmail(request.email)
            ?: throw ApiException(ExceptionType.DATA_NOT_FOUND)
        user.password = encryptPassword(request.password)
    }

    override fun verifyEmail(email: String, code: String) {
        val savedCode = authRedisStore.getEmailVerifyCode(email)
        if (savedCode != code)
            throw ApiException(ExceptionType.VALIDATION_ERROR)
        authRedisStore.setVerifiedEmail(email)
    }

    fun encryptPassword(password: String) : String {
        return bcryptEncoder.encode(password)
    }

}