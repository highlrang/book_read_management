package com.liber.book_read_management.service.recommendation.model

data class RecommendationTrace(
    val traceId: String,
    var plannerQueries: List<String> = emptyList(),
    var retrieverResultCount: Int = 0,
    var filterRemovedCount: Int = 0,
    var rankedResultCount: Int = 0,
    var plannerLatency: Long = 0,
    var retrieverLatency: Long = 0,
    var filterLatency: Long = 0,
    var rankerLatency: Long = 0,
    var explainerLatency: Long = 0
)
