package com.liber.book_read_management.service

import com.liber.book_read_management.auth.TokenUtil
import com.liber.book_read_management.dto.AuthResponse
import com.liber.book_read_management.dto.LoginRequest
import com.liber.book_read_management.dto.SignUpRequest
import com.liber.book_read_management.entities.User
import com.liber.book_read_management.exception.ApiException
import com.liber.book_read_management.exception.ExceptionType
import com.liber.book_read_management.repository.UserRepository
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

        val user = userRepository.save(User( // TODO factory method
            loginId = request.loginId,
            password = encryptPassword(request.password),
            name = request.name,
            phoneNumber = request.phoneNumber,
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