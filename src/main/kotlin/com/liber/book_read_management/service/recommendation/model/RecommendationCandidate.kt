package com.liber.book_read_management.service.recommendation.model

import com.liber.book_read_management.dto.aladin.AladinBookSearchResponse

data class RecommendationCandidate(
    val query: String,
    val item: AladinBookSearchResponse.AladinItem
)
