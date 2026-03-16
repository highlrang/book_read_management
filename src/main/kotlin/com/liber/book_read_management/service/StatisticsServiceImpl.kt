package com.liber.book_read_management.service

import com.liber.book_read_management.dto.DailyReadResponse
import com.liber.book_read_management.dto.GoalProgressResponse
import com.liber.book_read_management.dto.MonthlyPageResponse
import com.liber.book_read_management.dto.ReadingPaceResponse
import com.liber.book_read_management.dto.StatisticsResponse
import com.liber.book_read_management.dto.CategoryRatioResponse
import com.liber.book_read_management.enums.BookReadStatus
import com.liber.book_read_management.enums.CategoryGroup
import com.liber.book_read_management.repository.BookReadLogRepository
import com.liber.book_read_management.repository.BookReadProgressRepository
import com.liber.book_read_management.repository.UserRepository
import com.liber.book_read_management.util.CategoryLabel
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.LocalTime
import kotlin.math.ceil
import kotlin.math.round
import kotlin.math.roundToInt

@Service
class StatisticsServiceImpl(
    private val bookReadLogRepository: BookReadLogRepository,
    private val bookReadProgressRepository: BookReadProgressRepository,
    private val userRepository: UserRepository
) : StatisticsService {

    override fun getStatistics(userId: Long): StatisticsResponse {
        val readingCount = bookReadLogRepository.countByUserIdAndReadStatus(userId, BookReadStatus.READING)
        val completeCount = bookReadLogRepository.countByUserIdAndReadStatus(userId, BookReadStatus.COMPLETED)

        val now = LocalDate.now()
        val yearStart = LocalDate.of(now.year, 1, 1)
        val yearEnd = LocalDate.of(now.year, 12, 31)

        val thisMonthStart = now.withDayOfMonth(1)
        val thisMonthEnd = thisMonthStart.plusMonths(1).minusDays(1)
        val lastMonthStart = thisMonthStart.minusMonths(1)
        val lastMonthEnd = thisMonthStart.minusDays(1)

        val avgStart = now.minusDays(29)

        val progressList = bookReadProgressRepository
            .findAllByUserIdAndCreatedAtLessThanEqualOrderByBookReadLogIdAscIdAsc(
                userId,
                now.atTime(LocalTime.MAX)
            )

        val prevReadByBook = mutableMapOf<Long, Int>()
        val dailyTotalsAll = mutableMapOf<LocalDate, Int>()
        val dailyTotalsLast30 = mutableMapOf<LocalDate, Int>()
        var thisMonthPages = 0
        var lastMonthPages = 0
        var yearlyPages = 0

        for (progress in progressList) {
            val createdAt = progress.createdAt ?: continue
            val date = createdAt.toLocalDate()
            val bookId = progress.bookReadLogId

            val prevReadPage = prevReadByBook[bookId] ?: 0
            val diffPage = progress.readDiff.takeIf { it > 0 }
                ?: (progress.readPage - prevReadPage).coerceAtLeast(0)
            prevReadByBook[bookId] = progress.readPage

            if (diffPage == 0) continue

            dailyTotalsAll[date] = (dailyTotalsAll[date] ?: 0) + diffPage

            if (!date.isBefore(avgStart) && !date.isAfter(now)) {
                dailyTotalsLast30[date] = (dailyTotalsLast30[date] ?: 0) + diffPage
            }

            if (!date.isBefore(thisMonthStart) && !date.isAfter(thisMonthEnd)) {
                thisMonthPages += diffPage
            }

            if (!date.isBefore(lastMonthStart) && !date.isAfter(lastMonthEnd)) {
                lastMonthPages += diffPage
            }

            if (!date.isBefore(yearStart) && !date.isAfter(yearEnd)) {
                yearlyPages += diffPage
            }
        }

        val yearlyDailyReads = buildDailyReadsWithProgress(dailyTotalsAll)

        val streakDays = calculateStreakDays(dailyTotalsAll, now)
        val expectedStreakDays = calculateExpectedStreakDays(dailyTotalsAll, now)

        val totalLast30 = dailyTotalsLast30.values.sum()
        val averageDailyPagesRaw = totalLast30 / 30.0
        val averageDailyPages = round(averageDailyPagesRaw * 10) / 10.0
        val projectedMonthlyPages = (averageDailyPages * 30).roundToInt()
        val daysToRead300Pages = if (averageDailyPages > 0) {
            ceil(300.0 / averageDailyPages).toInt()
        } else {
            null
        }

        val monthlyReadDiff = thisMonthPages - lastMonthPages
        val monthlyReadDiffRatePercent = if (lastMonthPages > 0) {
            round((monthlyReadDiff.toDouble() / lastMonthPages.toDouble()) * 100).toInt()
        } else {
            null
        }

        val categoryTop3 = buildCategoryTop3(userId)

        val user = userRepository.findById(userId).orElse(null)
        val monthlyGoalAchievementRatePercent = if (user?.monthlyGoalPages != null && user.monthlyGoalPages!! > 0) {
            round((thisMonthPages.toDouble() / user.monthlyGoalPages!!.toDouble()) * 100).toInt()
        } else {
            null
        }
        val yearlyGoalAchievementRatePercent = if (user?.yearlyGoalPages != null && user.yearlyGoalPages!! > 0) {
            round((yearlyPages.toDouble() / user.yearlyGoalPages!!.toDouble()) * 100).toInt()
        } else {
            null
        }

        return StatisticsResponse(
            readingCount = readingCount,
            completeCount = completeCount,
            categoryTop3 = categoryTop3,
            monthlyPage = MonthlyPageResponse(
                currentMonthTotal = thisMonthPages,
                previousMonthTotal = lastMonthPages,
                diff = monthlyReadDiff,
                diffRatePercent = monthlyReadDiffRatePercent
            ),
            monthlyGoal = GoalProgressResponse(
                goalPages = user?.monthlyGoalPages,
                readPages = thisMonthPages,
                achievementRatePercent = monthlyGoalAchievementRatePercent
            ),
            yearlyGoal = GoalProgressResponse(
                goalPages = user?.yearlyGoalPages,
                readPages = yearlyPages,
                achievementRatePercent = yearlyGoalAchievementRatePercent
            ),
            yearlyDailyReads = yearlyDailyReads,
            streakDays = streakDays,
            expectedStreakDays = expectedStreakDays,
            readingPace = ReadingPaceResponse(
                averageDailyPages = averageDailyPages,
                projectedMonthlyPages = projectedMonthlyPages,
                daysToRead300Pages = daysToRead300Pages
            )
        )
    }

    private fun buildDailyReadsWithProgress(
        dailyTotals: Map<LocalDate, Int>
    ): List<DailyReadResponse> {
        return dailyTotals.entries
            .filter { it.value > 0 }
            .sortedBy { it.key }
            .map { entry ->
                DailyReadResponse(
                    date = entry.key,
                    pages = entry.value
                )
            }
    }

    private fun calculateStreakDays(dailyTotals: Map<LocalDate, Int>, today: LocalDate): Int {
        var streak = 0
        var cursor = today

        while (true) {
            val pages = dailyTotals[cursor] ?: 0
            if (pages <= 0) break
            streak += 1
            cursor = cursor.minusDays(1)
        }

        return streak
    }

    private fun calculateExpectedStreakDays(dailyTotals: Map<LocalDate, Int>, today: LocalDate): Int {
        val todayPages = dailyTotals[today] ?: 0
        if (todayPages > 0) {
            return calculateStreakDays(dailyTotals, today)
        }

        var streak = 1
        var cursor = today.minusDays(1)

        while (true) {
            val pages = dailyTotals[cursor] ?: 0
            if (pages <= 0) break
            streak += 1
            cursor = cursor.minusDays(1)
        }

        return streak
    }

    private fun buildCategoryTop3(userId: Long): List<CategoryRatioResponse> {
        val logs = bookReadLogRepository.findAllByUserIdAndCategoryGroupNotNull(userId)
        val counts = mutableMapOf<CategoryGroup, Int>()

        for (log in logs) {
            val group = log.categoryGroup ?: CategoryGroup.UNKNOWN
            counts[group] = (counts[group] ?: 0) + 1
        }

        if (counts.isEmpty()) return emptyList()

        val total = counts.values.sum()
        return counts.entries
            .sortedWith(compareByDescending<Map.Entry<CategoryGroup, Int>> { it.value }.thenBy { it.key.name })
            .take(3)
            .map { entry ->
                val percent = if (total > 0) {
                    round((entry.value.toDouble() / total.toDouble()) * 100).toInt()
                } else {
                    0
                }
                CategoryRatioResponse(CategoryLabel.toKorean(entry.key), entry.value, percent)
            }
    }
}
