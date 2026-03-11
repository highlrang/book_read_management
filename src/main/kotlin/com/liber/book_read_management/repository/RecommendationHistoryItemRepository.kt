package com.liber.book_read_management.repository

import com.liber.book_read_management.entities.RecommendationHistoryItem
import org.springframework.data.jpa.repository.JpaRepository

interface RecommendationHistoryItemRepository : JpaRepository<RecommendationHistoryItem, Long>
