package com.liber.book_read_management.service.recommendation.model

data class RankedRecommendation(
    val candidate: RecommendationCandidate,
    val score: Double,
    val keywordScore: Double,
    val publishDateScore: Double,
    val popularityScore: Double
)
