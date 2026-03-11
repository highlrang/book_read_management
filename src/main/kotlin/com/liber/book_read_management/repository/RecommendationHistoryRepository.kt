package com.liber.book_read_management.repository

import com.liber.book_read_management.entities.RecommendationHistory
import org.springframework.data.jpa.repository.JpaRepository

interface RecommendationHistoryRepository : JpaRepository<RecommendationHistory, Long>
