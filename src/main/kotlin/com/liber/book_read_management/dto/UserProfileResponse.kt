package com.liber.book_read_management.dto

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalTime

@Schema(description = "사용자 프로필 응답")
data class UserProfileResponse(
    @Schema(description = "닉네임", example = "독서왕")
    val nickname: String,
    @Schema(description = "프로필 이미지 URL", example = "http://localhost:9090/api/v1/files/10/content")
    val photoUrl: String?,
    @Schema(description = "독서 기록 시작 후 경과 일수", example = "32")
    val readingDays: Long,
    @Schema(description = "알림 활성화 여부", example = "true")
    val notificationEnabled: Boolean,
    @field:JsonFormat(pattern = "HH:mm")
    @field:Schema(description = "알림 시간(HH:mm)", example = "21:00")
    val notificationTime: LocalTime?,
    @Schema(description = "FCM 토큰 등록 여부", example = "true")
    val hasFcmToken: Boolean
)
