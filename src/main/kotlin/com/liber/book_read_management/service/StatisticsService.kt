package com.liber.book_read_management.service

import com.liber.book_read_management.dto.StatisticsResponse

interface StatisticsService {
    fun getStatistics(userId: Long): StatisticsResponse
}
