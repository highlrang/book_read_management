package com.liber.book_read_management.dto

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalTime

@Schema(description = "알림 설정 응답")
data class NotificationSettingResponse(
    @Schema(description = "알림 활성화 여부", example = "true")
    val enabled: Boolean,
    @field:JsonFormat(pattern = "HH:mm")
    @field:Schema(description = "알림 시간(HH:mm)", example = "21:00")
    val time: LocalTime?,
    @Schema(description = "FCM 토큰 등록 여부", example = "true")
    val hasFcmToken: Boolean
)
