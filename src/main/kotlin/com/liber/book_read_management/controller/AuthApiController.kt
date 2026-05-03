package com.liber.book_read_management.controller;

import com.liber.book_read_management.auth.CurrentUserId
import com.liber.book_read_management.dto.*
import com.liber.book_read_management.service.AuthEncryptionService
import com.liber.book_read_management.service.EmailService
import com.liber.book_read_management.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "인증 API", description = "사용자 인증 관련 API")
@RestController
@RequestMapping("/api/v1/auth")
class AuthApiController(
    private val userService: UserService,
    private val emailService: EmailService,
    private val authEncryptionService: AuthEncryptionService
) {

    @Operation(summary = "닉네임 중복 확인")
    @PostMapping("/nickname/check")
    fun checkNickname(@RequestBody request: NicknameCheckRequest) : ResponseEntity<ApiResponse<NicknameCheckResponse>> {
        return ResponseEntity.ok(
            ApiResponse.success(userService.checkNickname(request.nickname))
        )
    }

    @Operation(summary = "인증 공개키 조회", description = "RSA 공개키와 비밀번호 암호화 정보를 조회합니다.")
    @GetMapping("/public-key")
    fun getPublicKey(): ResponseEntity<ApiResponse<AuthPublicKeyResponse>> {
        return ResponseEntity.ok(
            ApiResponse.success(authEncryptionService.getPublicKey())
        )
    }

    @Operation(
        summary = "비밀번호 암호화",
        description = "서버 테스트 및 Swagger 검증용 보조 API입니다. 실제 앱에서는 클라이언트가 공개키로 직접 암호화해야 합니다."
    )
    @PostMapping("/encrypt-password")
    fun encryptPassword(@RequestBody request: EncryptPasswordRequest): ResponseEntity<ApiResponse<EncryptPasswordResponse>> {
        return ResponseEntity.ok(
            ApiResponse.success(authEncryptionService.encryptPassword(request.password))
        )
    }

    @Operation(summary = "회원가입", description = "사용자 정보를 입력받아 회원가입을 처리합니다.")
    @PostMapping("/sign-up")
    fun signUp(@RequestBody request: SignUpRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        return ResponseEntity.ok(
            ApiResponse.success(userService.signUp(request))
        )
    }

    @Operation(summary = "로그인", description = "이메일과 비밀번호를 입력받아 로그인을 처리합니다.")
    @PostMapping("/login")
    fun login(@RequestBody request: LoginRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        return ResponseEntity.ok(
            ApiResponse.success(userService.login(request))
        )
    }

    @Operation(summary = "로그아웃", description = "로그아웃을 처리합니다.")
    @PostMapping("/logout")
    fun logout(@CurrentUserId userId: Long) : ResponseEntity<ApiResponse<Unit>> {
        userService.logout(userId)
        return ResponseEntity.ok(ApiResponse.success())
    }

    @Operation(summary = "토큰 재발급", description = "리프레시 토큰으로 액세스 토큰을 재발급합니다.")
    @PostMapping("/refresh")
    fun refreshToken(@RequestBody request: RefreshTokenRequest): ResponseEntity<ApiResponse<AuthResponse>> {
        return ResponseEntity.ok(
            ApiResponse.success(userService.refreshToken(request.refreshToken))
        )
    }

    @Operation(summary = "인증 메일 발송", description = "인증 메일을 발송합니다.")
    @PostMapping("/send-verification-email")
    fun sendVerificationEmail(@RequestBody request: SendEmailRequest): ResponseEntity<ApiResponse<Unit>> {
        emailService.sendVerificationEmail(request.email)
        return ResponseEntity.ok(ApiResponse.success())
    }

    @Operation(summary = "이메일 인증 토큰 검증", description = "앱 딥링크에서 전달받은 이메일 인증 토큰을 검증합니다.")
    @GetMapping("/verify-email")
    fun verifyEmail(@RequestParam token: String): ResponseEntity<ApiResponse<Unit>> {
        userService.verifyEmailToken(token)
        return ResponseEntity.ok(ApiResponse.success())
    }

    @Operation(summary = "비밀번호 변경", description = "비밀번호를 변경합니다.")
    @PostMapping("/password")
    fun updatePassword(@RequestBody request: UpdatePasswordRequest): ResponseEntity<ApiResponse<Unit>> {
        userService.updatePassword(request)
        return ResponseEntity.ok(ApiResponse.success())
    }

}
