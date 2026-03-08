package com.liber.book_read_management.controller

import com.liber.book_read_management.auth.CurrentUserId
import com.liber.book_read_management.dto.ApiResponse
import com.liber.book_read_management.dto.NotificationSettingResponse
import com.liber.book_read_management.dto.NotificationSettingUpsertRequest
import com.liber.book_read_management.dto.UserProfileResponse
import com.liber.book_read_management.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "프로필 API", description = "프로필/알림 설정 관련 API")
@RestController
@RequestMapping("/api/v1/profile")
class UserProfileApiController(
    private val userService: UserService
) {

    @Operation(summary = "프로필 조회", description = "프로필 이미지, 이름, 독서 기록 시작 후 경과 일수를 조회합니다.")
    @GetMapping
    fun getProfile(@CurrentUserId userId: Long): ResponseEntity<ApiResponse<UserProfileResponse>> {
        return ResponseEntity.ok(ApiResponse.success(userService.getProfile(userId)))
    }

    @Operation(summary = "알림 설정 저장", description = "알림 on/off, 알림 시간, FCM 토큰을 저장합니다.")
    @PutMapping("/notification")
    fun upsertNotification(
        @CurrentUserId userId: Long,
        @RequestBody request: NotificationSettingUpsertRequest
    ): ResponseEntity<ApiResponse<NotificationSettingResponse>> {
        return ResponseEntity.ok(ApiResponse.success(userService.upsertNotificationSetting(userId, request)))
    }
}
