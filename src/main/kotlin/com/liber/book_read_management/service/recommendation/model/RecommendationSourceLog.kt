package com.liber.book_read_management.service.recommendation.model

import com.liber.book_read_management.entities.RecommendationSourceType

data class RecommendationSourceLog(
    val bookReadLogId: Long,
    val sourceType: RecommendationSourceType
)
