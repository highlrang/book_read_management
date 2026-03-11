package com.liber.book_read_management.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "RECOMMENDATION_HISTORY_SOURCE_LOG")
class RecommendationHistorySourceLog(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "recommendation_history_id", nullable = false)
    var recommendationHistoryId: Long,

    @Column(name = "book_read_log_id", nullable = false)
    var bookReadLogId: Long,

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false, length = 50)
    var sourceType: RecommendationSourceType
) : BaseTimeEntity()
