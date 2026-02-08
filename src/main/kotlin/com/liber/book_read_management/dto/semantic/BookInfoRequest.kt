package com.ollama.semantic_api.dto

data class BookInfoRequest(
    val title: String,
    val introduction: String,
    val reviews: List<String>
)
