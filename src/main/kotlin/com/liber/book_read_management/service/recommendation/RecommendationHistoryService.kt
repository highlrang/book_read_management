package com.liber.book_read_management.service.recommendation

import com.liber.book_read_management.entities.RecommendationHistory
import com.liber.book_read_management.entities.RecommendationHistoryItem
import com.liber.book_read_management.entities.RecommendationHistorySourceLog
import com.liber.book_read_management.repository.RecommendationHistoryItemRepository
import com.liber.book_read_management.repository.RecommendationHistoryRepository
import com.liber.book_read_management.repository.RecommendationHistorySourceLogRepository
import com.liber.book_read_management.service.recommendation.model.RecommendationPipelineResult
import com.liber.book_read_management.service.recommendation.model.RecommendationSourceLog
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RecommendationHistoryService(
    private val recommendationHistoryRepository: RecommendationHistoryRepository,
    private val recommendationHistoryItemRepository: RecommendationHistoryItemRepository,
    private val recommendationHistorySourceLogRepository: RecommendationHistorySourceLogRepository
) {

    @Transactional
    fun save(
        traceId: String,
        userId: Long,
        sourceLogs: List<RecommendationSourceLog>,
        cacheHit: Boolean,
        pipelineResult: RecommendationPipelineResult
    ) {
        val history = recommendationHistoryRepository.save(
            RecommendationHistory(
                traceId = traceId,
                userId = userId,
                requestTitle = pipelineResult.requestTitle,
                plannerQueriesJson = pipelineResult.plannerQueries.joinToString(
                    prefix = "[",
                    postfix = "]",
                    separator = ","
                ) { "\"$it\"" },
                plannerModel = pipelineResult.plannerModel,
                plannerPromptTokenCount = pipelineResult.plannerPromptTokenCount,
                plannerResponseTokenCount = pipelineResult.plannerResponseTokenCount,
                plannerTotalTokenCount = pipelineResult.plannerTotalTokenCount,
                explainerModel = pipelineResult.explainerModel,
                explainerPromptTokenCount = pipelineResult.explainerPromptTokenCount,
                explainerResponseTokenCount = pipelineResult.explainerResponseTokenCount,
                explainerTotalTokenCount = pipelineResult.explainerTotalTokenCount,
                retrieverResultCount = pipelineResult.retrieverResultCount,
                filterRemovedCount = pipelineResult.filterRemovedCount,
                rankedResultCount = pipelineResult.rankedResultCount,
                plannerLatency = pipelineResult.plannerLatency,
                retrieverLatency = pipelineResult.retrieverLatency,
                filterLatency = pipelineResult.filterLatency,
                rankerLatency = pipelineResult.rankerLatency,
                explainerLatency = pipelineResult.explainerLatency,
                retryCount = pipelineResult.retryCount,
                cacheHit = cacheHit
            )
        )

        val items = pipelineResult.rankedRecommendations.mapIndexed { index, ranked ->
            val item = ranked.candidate.item
            val isbn = item.isbn13?.takeIf { it.isNotBlank() } ?: item.isbn.orEmpty()

            RecommendationHistoryItem(
                recommendationHistoryId = history.id!!,
                rankOrder = index + 1,
                isbn = isbn,
                title = item.title.orEmpty(),
                author = item.author.orEmpty(),
                matchedQuery = ranked.candidate.query,
                score = ranked.score,
                keywordScore = ranked.keywordScore,
                publishDateScore = ranked.publishDateScore,
                popularityScore = ranked.popularityScore,
                recommendationReason = pipelineResult.explanationsByIsbn[isbn]
            )
        }

        recommendationHistoryItemRepository.saveAll(items)

        val sourceLogEntities = sourceLogs.map { sourceLog ->
            RecommendationHistorySourceLog(
                recommendationHistoryId = history.id!!,
                bookReadLogId = sourceLog.bookReadLogId,
                sourceType = sourceLog.sourceType
            )
        }
        recommendationHistorySourceLogRepository.saveAll(sourceLogEntities)
    }
}
