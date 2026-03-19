package com.liber.book_read_management.controller

import com.liber.book_read_management.auth.CurrentUserId
import com.liber.book_read_management.dto.ApiResponse
import com.liber.book_read_management.dto.DailyReadResponse
import com.liber.book_read_management.dto.StatisticsResponse
import com.liber.book_read_management.service.StatisticsService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "통계 API", description = "독서 통계 관련 API")
@RestController
@RequestMapping("/api/v1/statistics")
class StatisticsApiController(
    private val statisticsService: StatisticsService
) {

    @Operation(summary = "독서 통계 조회", description = "읽기 상태, 월간/연간 통계, 스트릭, 평균 독서 속도를 조회합니다.")
    @GetMapping
    fun getStatistics(@CurrentUserId userId: Long): ResponseEntity<ApiResponse<StatisticsResponse>> {
        val response = statisticsService.getStatistics(userId)
        return ResponseEntity.ok(ApiResponse.success(response))
    }

    @Operation(summary = "연도별 잔디 데이터 조회", description = "특정 연도의 일별 읽은 페이지 목록을 조회합니다.")
    @GetMapping("/daily-reads")
    fun getYearlyDailyReads(
        @CurrentUserId userId: Long,
        @RequestParam(required = false) year: Int?
    ): ResponseEntity<ApiResponse<List<DailyReadResponse>>> {
        val response = statisticsService.getYearlyDailyReads(userId, year)
        return ResponseEntity.ok(ApiResponse.success(response))
    }
}
