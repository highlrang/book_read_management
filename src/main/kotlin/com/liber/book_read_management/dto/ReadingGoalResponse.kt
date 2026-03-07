package com.liber.book_read_management.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "독서 목표 응답")
data class ReadingGoalResponse(
    @Schema(description = "월간 목표 페이지", example = "1200")
    val monthlyGoalPages: Int?,
    @Schema(description = "연간 목표 페이지", example = "12000")
    val yearlyGoalPages: Int?
)
