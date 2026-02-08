package com.liber.book_read_management.dto.semantic

data class SemanticScore(
    val abstraction: Double,
    val emotion: Double,
    val challenge: Double,
    val narrative: Double,
    val confidence: Double
)
