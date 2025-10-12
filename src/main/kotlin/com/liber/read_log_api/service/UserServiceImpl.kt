package com.liber.read_log_api.service

import com.liber.read_log_api.auth.TokenUtil
import com.liber.read_log_api.dto.AuthResponse
import com.liber.read_log_api.dto.LoginRequest
import com.liber.read_log_api.dto.SignUpRequest
import com.liber.read_log_api.entities.User
import com.liber.read_log_api.exception.ApiException
import com.liber.read_log_api.exception.ExceptionType
import com.liber.read_log_api.repository.UserRepository
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserServiceImpl(
    private var userRepository: UserRepository,
    private var bcryptEncoder: BCryptPasswordEncoder

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
            address = request.address,
            addressDetail = request.addressDetail,
            addressLatitude = request.addressLatitude,
            addressLongitude = request.addressLongitude
        ))

        val accessToken = TokenUtil.createToken(requireNotNull(user.id))
        // TODO requireNotNull 또는 TokenUtil.createToken(user.id!!)
        user.accessToken = accessToken

        return AuthResponse(user.id!!, accessToken)
    }

    @Transactional
    override fun login(request: LoginRequest) : AuthResponse {
        val user = userRepository.findByLoginId(request.loginId) ?:
            throw ApiException(ExceptionType.DATA_NOT_FOUND)

        if (!bcryptEncoder.matches(request.password, user.password))
            throw ApiException(ExceptionType.PASSWORD_NOT_MATCHED)

        val accessToken = TokenUtil.createToken(requireNotNull(user.id))
        user.accessToken = accessToken

        return AuthResponse(user.id!!, accessToken)
    }

    @Transactional
    override fun logout(userId: Long) {
        val user : User = userRepository.findById(userId)
            .orElseThrow { throw ApiException(ExceptionType.DATA_NOT_FOUND) }

        user.accessToken = null
    }

    fun encryptPassword(password: String) : String {
        return bcryptEncoder.encode(password)
    }

}