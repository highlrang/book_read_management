package com.liber.book_read_management.service.recommendation.model

data class RecommendationPipelineResult(
    val requestTitle: String,
    val plannerQueries: List<String>,
    val plannerModel: String,
    val plannerPromptTokenCount: Int?,
    val plannerResponseTokenCount: Int?,
    val plannerTotalTokenCount: Int?,
    val explainerModel: String,
    val explainerPromptTokenCount: Int?,
    val explainerResponseTokenCount: Int?,
    val explainerTotalTokenCount: Int?,
    val retrieverResultCount: Int,
    val filterRemovedCount: Int,
    val rankedResultCount: Int,
    val plannerLatency: Long,
    val retrieverLatency: Long,
    val filterLatency: Long,
    val rankerLatency: Long,
    val explainerLatency: Long,
    val retryCount: Int,
    val rankedRecommendations: List<RankedRecommendation>,
    val explanationsByIsbn: Map<String, String>
)
