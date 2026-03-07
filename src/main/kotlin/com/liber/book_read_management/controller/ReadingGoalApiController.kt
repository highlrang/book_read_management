package com.liber.book_read_management.controller

import com.liber.book_read_management.auth.CurrentUserId
import com.liber.book_read_management.dto.ApiResponse
import com.liber.book_read_management.dto.ReadingGoalRequest
import com.liber.book_read_management.dto.ReadingGoalResponse
import com.liber.book_read_management.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "독서 목표 API", description = "독서 목표(월간/연간) 관련 API")
@RestController
@RequestMapping("/api/v1/reading-goal")
class ReadingGoalApiController(
    private val userService: UserService
) {

    @Operation(summary = "독서 목표 조회", description = "월간/연간 독서 목표를 조회합니다.")
    @GetMapping
    fun getReadingGoal(@CurrentUserId userId: Long): ResponseEntity<ApiResponse<ReadingGoalResponse>> {
        val response = userService.getReadingGoal(userId)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @Operation(summary = "독서 목표 설정", description = "월간/연간 독서 목표를 저장(Upsert)합니다.")
    @PutMapping
    fun upsertReadingGoal(
        @CurrentUserId userId: Long,
        @Valid @RequestBody request: ReadingGoalRequest
    ): ResponseEntity<ApiResponse<ReadingGoalResponse>> {
        val response = userService.upsertReadingGoal(userId, request)
        return ResponseEntity.ok(ApiResponse.success(response))
    }
}
