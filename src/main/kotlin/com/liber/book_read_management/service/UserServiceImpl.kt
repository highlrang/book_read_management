package com.liber.book_read_management.service

import com.liber.book_read_management.auth.TokenUtil
import com.liber.book_read_management.dto.*
import com.liber.book_read_management.entities.User
import com.liber.book_read_management.exception.ApiException
import com.liber.book_read_management.exception.ExceptionType
import com.liber.book_read_management.repository.BookReadLogRepository
import com.liber.book_read_management.repository.UserRepository
import com.liber.book_read_management.repository.redis.AuthRedisStore
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Service
class UserServiceImpl(
    private var userRepository: UserRepository,
    private val bookReadLogRepository: BookReadLogRepository,
    private val fileService: FileService,
    private var bcryptEncoder: BCryptPasswordEncoder,
    private var tokenUtil: TokenUtil,
    private val authRedisStore: AuthRedisStore,
    private val emailVerificationService: EmailVerificationService,
    private val pushNotificationService: PushNotificationService,
    private val authEncryptionService: AuthEncryptionService,
    private val tokenHashService: TokenHashService
) : UserService {

    override fun checkNickname(nickname: String): NicknameCheckResponse {
        return NicknameCheckResponse(!userRepository.existsByNickname(nickname))
    }

    @Transactional
    override fun signUp(request: SignUpRequest): AuthResponse {
        val rawPassword = authEncryptionService.resolvePassword(request.password, request.encryptedPassword)
        val existUser = userRepository.findByEmail(request.email)
        if (existUser != null) {
            throw ApiException(ExceptionType.ALREADY_EXIST)
        }

        if (!emailVerificationService.isVerified(request.email) && !authRedisStore.isVerifiedEmail(request.email)) {
            throw ApiException(ExceptionType.VALIDATION_ERROR)
        }

        val user = userRepository.save(User(
            email = request.email,
            password = encryptPassword(rawPassword),
            nickname = request.nickname,
            photoId = request.photoId
        ))
        val userId = user.id!!
        val accessToken = tokenUtil.createAccessToken(userId)
        val refreshToken = tokenUtil.createRefreshToken(userId)
        user.accessToken = tokenHashService.hash(accessToken)
        user.refreshToken = tokenHashService.hash(refreshToken)

        return AuthResponse(userId, accessToken, refreshToken)
    }

    @Transactional
    override fun login(request: LoginRequest) : AuthResponse {
        val rawPassword = authEncryptionService.resolvePassword(request.password, request.encryptedPassword)
        val user = userRepository.findByEmail(request.email) ?:
            throw ApiException(ExceptionType.DATA_NOT_FOUND)

        if (!bcryptEncoder.matches(rawPassword, user.password))
            throw ApiException(ExceptionType.PASSWORD_NOT_MATCHED)

        val userId = user.id!!
        val accessToken = tokenUtil.createAccessToken(userId)
        val refreshToken = tokenUtil.createRefreshToken(userId)

        user.accessToken = tokenHashService.hash(accessToken)
        user.refreshToken = tokenHashService.hash(refreshToken)
        if (!request.fcmToken.isNullOrBlank()) {
            user.fcmToken = request.fcmToken
        }

        return AuthResponse(userId, accessToken, refreshToken)
    }

    @Transactional
    override fun logout(userId: Long) {
        val user : User = userRepository.findById(userId)
            .orElseThrow { throw ApiException(ExceptionType.DATA_NOT_FOUND) }

        user.accessToken = null
        user.refreshToken = null
    }

    @Transactional
    override fun refreshToken(refreshToken: String): AuthResponse {
        val decodedJWT = tokenUtil.verifyToken(refreshToken)
        val userId = decodedJWT.subject.toLong()
        val user = userRepository.findById(userId)
            .orElseThrow { throw ApiException(ExceptionType.DATA_NOT_FOUND) }

        if (!tokenHashService.matches(refreshToken, user.refreshToken)) {
            throw ApiException(ExceptionType.INVALID_AUTH)
        }

        val newAccessToken = tokenUtil.createAccessToken(user.id!!)
        val newRefreshToken = tokenUtil.createRefreshToken(user.id!!)

        user.accessToken = tokenHashService.hash(newAccessToken)
        user.refreshToken = tokenHashService.hash(newRefreshToken)

        return AuthResponse(user.id!!, newAccessToken, newRefreshToken)
    }

    @Transactional
    override fun updatePassword(request: UpdatePasswordRequest) {
        val rawPassword = authEncryptionService.resolvePassword(request.password, request.encryptedPassword)
        val user = userRepository.findByEmail(request.email)
            ?: throw ApiException(ExceptionType.DATA_NOT_FOUND)
        user.password = encryptPassword(rawPassword)
    }

    override fun verifyEmailToken(token: String) {
        emailVerificationService.verifyToken(token)
    }

    override fun verifyEmail(token: String?, email: String?, code: String?) {
        if (!token.isNullOrBlank()) {
            verifyEmailToken(token)
            return
        }

        if (!email.isNullOrBlank() && !code.isNullOrBlank()) {
            val savedCode = authRedisStore.getEmailVerifyCode(email)
            if (savedCode != code) {
                throw ApiException(ExceptionType.VALIDATION_ERROR)
            }
            authRedisStore.setVerifiedEmail(email)
            return
        }

        throw ApiException(ExceptionType.VALIDATION_ERROR)
    }

    @Transactional(readOnly = true)
    override fun getReadingGoal(userId: Long): ReadingGoalResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { throw ApiException(ExceptionType.DATA_NOT_FOUND) }
        return ReadingGoalResponse(user.monthlyGoalPages, user.yearlyGoalPages)
    }

    @Transactional
    override fun upsertReadingGoal(userId: Long, request: ReadingGoalRequest): ReadingGoalResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { throw ApiException(ExceptionType.DATA_NOT_FOUND) }
        user.monthlyGoalPages = request.monthlyGoalPages
        user.yearlyGoalPages = request.yearlyGoalPages
        return ReadingGoalResponse(user.monthlyGoalPages, user.yearlyGoalPages)
    }

    @Transactional(readOnly = true)
    override fun getProfile(userId: Long): UserProfileResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { throw ApiException(ExceptionType.DATA_NOT_FOUND) }

        val firstReadLog = bookReadLogRepository.findTopByUserIdOrderByCreatedAtAsc(userId)
        val readingDays = if (firstReadLog?.createdAt != null) {
            ChronoUnit.DAYS.between(firstReadLog.createdAt!!.toLocalDate(), LocalDate.now()) + 1
        } else {
            0L
        }
        val photoPath = fileService.getFilePath(user.photoId)

        return UserProfileResponse(
            nickname = user.nickname,
            photoUrl = fileService.getFileUrl(photoPath),
            readingDays = readingDays,
            notificationEnabled = user.notificationEnabled,
            notificationTime = user.notificationTime,
            hasFcmToken = !user.fcmToken.isNullOrBlank()
        )
    }

    @Transactional
    override fun upsertNotificationSetting(
        userId: Long,
        request: NotificationSettingUpsertRequest
    ): NotificationSettingResponse {
        if (request.enabled && request.time == null) {
            throw ApiException(ExceptionType.VALIDATION_ERROR)
        }

        val user = userRepository.findById(userId)
            .orElseThrow { throw ApiException(ExceptionType.DATA_NOT_FOUND) }

        user.notificationEnabled = request.enabled
        user.notificationTime = if (request.enabled) request.time else null

        if (user.notificationEnabled && !user.fcmToken.isNullOrBlank()) {
            pushNotificationService.sendNotification(
                user.fcmToken!!,
                "독서 알림 설정 완료",
                "매일 ${user.notificationTime}에 독서 알림을 보내드릴게요."
            )
        }

        return NotificationSettingResponse(
            enabled = user.notificationEnabled,
            time = user.notificationTime,
            hasFcmToken = !user.fcmToken.isNullOrBlank()
        )
    }

    fun encryptPassword(password: String) : String {
        return bcryptEncoder.encode(password)
    }

}
