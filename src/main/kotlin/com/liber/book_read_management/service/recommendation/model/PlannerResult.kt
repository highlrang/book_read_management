package com.liber.book_read_management.service.recommendation.model

data class PlannerResult(
    val model: String,
    val promptTokenCount: Int?,
    val responseTokenCount: Int?,
    val totalTokenCount: Int?,
    val queries: List<String>
)
