package com.liber.book_read_management.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "연간 일별 읽은 페이지 응답")
data class DailyReadResponse(
    @Schema(description = "날짜", example = "2026-01-01")
    val date: java.time.LocalDate,
    @Schema(description = "해당 날짜 읽은 페이지", example = "25")
    val pages: Int
)

@Schema(description = "독서 통계 응답")
data class StatisticsResponse(
    @Schema(description = "읽는 중 도서 수", example = "3")
    val readingCount: Long,
    @Schema(description = "완료 도서 수", example = "12")
    val completeCount: Long,
    @Schema(description = "장르 비중 Top3")
    val categoryTop3: List<CategoryRatioResponse>,
    @Schema(description = "월간 읽은 페이지 지표")
    val monthlyPage: MonthlyPageResponse,
    @Schema(description = "월간 목표 달성 정보")
    val monthlyGoal: GoalProgressResponse,
    @Schema(description = "연간 목표 달성 정보")
    val yearlyGoal: GoalProgressResponse,
    @Schema(description = "연속 읽기 일수", example = "7")
    val streakDays: Int,
    @Schema(description = "독서 페이스 지표")
    val readingPace: ReadingPaceResponse
)

@Schema(description = "목표 달성 정보")
data class GoalProgressResponse(
    @Schema(description = "목표 페이지", example = "1200")
    val goalPages: Int?,
    @Schema(description = "읽은 페이지", example = "800")
    val readPages: Int,
    @Schema(description = "달성률(%)", example = "67")
    val achievementRatePercent: Int?
)

@Schema(description = "장르 비중 응답")
data class CategoryRatioResponse(
    @Schema(description = "장르(한글)", example = "문학")
    val categoryGroup: String,
    @Schema(description = "권수", example = "8")
    val count: Int,
    @Schema(description = "비율(%)", example = "50")
    val percent: Int
)

@Schema(description = "월간 읽은 페이지 요약")
data class MonthlyPageResponse(
    @Schema(description = "이번달 읽은 페이지 합계", example = "1247")
    val currentMonthTotal: Int,
    @Schema(description = "지난달 읽은 페이지 합계", example = "980")
    val previousMonthTotal: Int,
    @Schema(description = "전월 대비 증감", example = "267")
    val diff: Int,
    @Schema(description = "전월 대비 증가율(%)", example = "27")
    val diffRatePercent: Int?
)

@Schema(description = "독서 페이스 응답")
data class ReadingPaceResponse(
    @Schema(description = "최근 30일 평균 일일 페이지", example = "23.4")
    val averageDailyPages: Double,
    @Schema(description = "평균 기준 월간 예상 페이지", example = "702")
    val projectedMonthlyPages: Int,
    @Schema(description = "평균 기준 300페이지 소요 일수", example = "13")
    val daysToRead300Pages: Int?
)
