package com.ollama.semantic_api.dto

data class SemanticScore(
    val abstraction: Double,
    val emotion: Double,
    val challenge: Double,
    val narrative: Double,
    val confidence: Double
)
