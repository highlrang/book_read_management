package com.liber.book_read_management.dto

import com.fasterxml.jackson.annotation.JsonFormat
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalTime

@Schema(description = "알림 설정 저장 요청")
data class NotificationSettingUpsertRequest(
    @Schema(description = "알림 활성화 여부", example = "true")
    val enabled: Boolean,
    @field:JsonFormat(pattern = "HH:mm")
    @field:Schema(description = "알림 시간(HH:mm)", example = "21:00")
    val time: LocalTime?
)
