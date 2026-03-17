package com.liber.book_read_management.service

import com.liber.book_read_management.dto.DailyReadResponse
import com.liber.book_read_management.dto.StatisticsResponse

interface StatisticsService {
    fun getStatistics(userId: Long): StatisticsResponse
    fun getYearlyDailyReads(userId: Long, year: Int): List<DailyReadResponse>
}
