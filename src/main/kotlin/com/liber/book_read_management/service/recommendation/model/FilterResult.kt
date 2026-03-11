package com.liber.book_read_management.service.recommendation.model

data class FilterResult(
    val candidates: List<RecommendationCandidate>,
    val removedCount: Int
)
