package com.liber.book_read_management.service

import com.liber.book_read_management.auth.TokenUtil
import com.liber.book_read_management.config.REFRESH_TOKEN_EXPIRATION_TIME
import com.liber.book_read_management.dto.AuthResponse
import com.liber.book_read_management.dto.LoginRequest
import com.liber.book_read_management.dto.SignUpRequest
import com.liber.book_read_management.entities.User
import com.liber.book_read_management.exception.ApiException
import com.liber.book_read_management.exception.ExceptionType
import com.liber.book_read_management.repository.RedisTemplateRepository
import com.liber.book_read_management.repository.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

@Service
class UserServiceImpl(
    private var userRepository: UserRepository,
    private var bcryptEncoder: BCryptPasswordEncoder,
    private var tokenUtil: TokenUtil
) : UserService {

    @Transactional
    override fun signUp(request: SignUpRequest): AuthResponse {
        val existUser = userRepository.findByLoginId(request.loginId)
        if (existUser != null) {
            throw ApiException(ExceptionType.DATA_NOT_FOUND)
        }

        val user = userRepository.save(User(
            loginId = request.loginId,
            password = encryptPassword(request.password),
            name = request.name,
            phoneNumber = request.phoneNumber,
            gender = request.gender
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
        val user = userRepository.findByLoginId(request.loginId) ?:
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

    fun encryptPassword(password: String) : String {
        return bcryptEncoder.encode(password)
    }

}