package com.liber.book_read_management.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

@Schema(description = "독서 목표 설정 요청")
class ReadingGoalRequest(
    @Schema(description = "월간 목표 페이지", example = "1200")
    @field:NotNull
    @field:Min(0)
    val monthlyGoalPages: Int,
    @Schema(description = "연간 목표 페이지", example = "12000")
    @field:NotNull
    @field:Min(0)
    val yearlyGoalPages: Int
)
