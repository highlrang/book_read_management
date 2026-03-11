package com.liber.book_read_management.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Lob
import jakarta.persistence.Table

@Entity
@Table(name = "RECOMMENDATION_HISTORY_ITEM")
class RecommendationHistoryItem(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "recommendation_history_id", nullable = false)
    var recommendationHistoryId: Long,

    @Column(name = "rank_order", nullable = false)
    var rankOrder: Int,

    @Column(name = "isbn", nullable = false, length = 50)
    var isbn: String,

    @Column(name = "title", nullable = false, length = 500)
    var title: String,

    @Column(name = "author", nullable = false, length = 500)
    var author: String,

    @Column(name = "matched_query", nullable = false, length = 500)
    var matchedQuery: String,

    @Column(name = "score", nullable = false)
    var score: Double,

    @Column(name = "keyword_score", nullable = false)
    var keywordScore: Double,

    @Column(name = "publish_date_score", nullable = false)
    var publishDateScore: Double,

    @Column(name = "popularity_score", nullable = false)
    var popularityScore: Double,

    @Lob
    @Column(name = "recommendation_reason")
    var recommendationReason: String?
) : BaseTimeEntity()
