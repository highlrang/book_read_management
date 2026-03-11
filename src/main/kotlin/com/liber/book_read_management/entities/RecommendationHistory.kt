package com.liber.book_read_management.entities

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "RECOMMENDATION_HISTORY")
class RecommendationHistory(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(name = "trace_id", nullable = false, unique = true, length = 100)
    var traceId: String,

    @Column(name = "user_id", nullable = false)
    var userId: Long,

    @Column(name = "request_title", nullable = false, length = 500)
    var requestTitle: String,

    @Column(name = "planner_queries_json", nullable = false)
    var plannerQueriesJson: String,

    @Column(name = "planner_model", nullable = false, length = 100)
    var plannerModel: String,

    @Column(name = "planner_prompt_token_count")
    var plannerPromptTokenCount: Int? = null,

    @Column(name = "planner_response_token_count")
    var plannerResponseTokenCount: Int? = null,

    @Column(name = "planner_total_token_count")
    var plannerTotalTokenCount: Int? = null,

    @Column(name = "explainer_model", nullable = false, length = 100)
    var explainerModel: String,

    @Column(name = "explainer_prompt_token_count")
    var explainerPromptTokenCount: Int? = null,

    @Column(name = "explainer_response_token_count")
    var explainerResponseTokenCount: Int? = null,

    @Column(name = "explainer_total_token_count")
    var explainerTotalTokenCount: Int? = null,

    @Column(name = "retriever_result_count", nullable = false)
    var retrieverResultCount: Int,

    @Column(name = "filter_removed_count", nullable = false)
    var filterRemovedCount: Int,

    @Column(name = "ranked_result_count", nullable = false)
    var rankedResultCount: Int,

    @Column(name = "planner_latency", nullable = false)
    var plannerLatency: Long,

    @Column(name = "retriever_latency", nullable = false)
    var retrieverLatency: Long,

    @Column(name = "filter_latency", nullable = false)
    var filterLatency: Long,

    @Column(name = "ranker_latency", nullable = false)
    var rankerLatency: Long,

    @Column(name = "explainer_latency", nullable = false)
    var explainerLatency: Long,

    @Column(name = "retry_count", nullable = false)
    var retryCount: Int,

    @Column(name = "cache_hit", nullable = false)
    var cacheHit: Boolean
) : BaseTimeEntity()
