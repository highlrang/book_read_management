package com.liber.book_read_management.service.recommendation

import com.liber.book_read_management.dto.BookSearchResponse
import com.liber.book_read_management.dto.PageResponse
import com.liber.book_read_management.dto.semantic.BookInfoRequest
import com.liber.book_read_management.repository.BookReadLogRepository
import com.liber.book_read_management.service.recommendation.model.RecommendationPipelineResult
import com.liber.book_read_management.service.recommendation.model.RecommendationSourceLog
import com.liber.book_read_management.service.recommendation.model.RecommendationTrace
import mu.KotlinLogging
import net.logstash.logback.argument.StructuredArguments.entries
import org.springframework.cache.CacheManager
import org.springframework.stereotype.Service
import java.util.UUID

private val log = KotlinLogging.logger {}

/*
 * 도서 추천 오케스트레이션
 * PlannerService -> RetrieverService -> FilterService -> RankerService -> ExplainerService 순서로 동작
 */
@Service
class RecommendationAgentService(
    private val cacheManager: CacheManager,
    private val plannerService: PlannerService,
    private val retrieverService: RetrieverService,
    private val filterService: FilterService,
    private val rankerService: RankerService,
    private val explainerService: ExplainerService,
    private val recommendationHistoryService: RecommendationHistoryService,
    private val bookReadLogRepository: BookReadLogRepository
) {
    fun recommend(
        userId: Long,
        request: BookInfoRequest,
        sourceLogs: List<RecommendationSourceLog>
    ): PageResponse<List<BookSearchResponse>> {
        val trace = RecommendationTrace(traceId = UUID.randomUUID().toString())
        val readIsbns = bookReadLogRepository.findBookIsbnsByUserId(userId)
        val cacheKey = RecommendationCacheKeyGenerator.generate(userId, request, readIsbns)
        val recommendationCache = cacheManager.getCache("recommendations")
        val cachedResult = recommendationCache?.get(cacheKey, RecommendationPipelineResult::class.java)

        val pipelineResult = if (cachedResult != null) {
            log.info(
                "recommendation.cache.hit {}",
                entries(
                    linkedMapOf(
                        "traceId" to trace.traceId,
                        "cacheKey" to cacheKey
                    )
                )
            )
            cachedResult
        } else {
            val computed = runPipeline(trace, userId, request, readIsbns)
            recommendationCache?.put(cacheKey, computed)
            computed
        }

        recommendationHistoryService.save(
            traceId = trace.traceId,
            userId = userId,
            sourceLogs = sourceLogs,
            cacheHit = cachedResult != null,
            pipelineResult = pipelineResult
        )

        val books = pipelineResult.rankedRecommendations.map { ranked ->
            BookSearchResponse.of(ranked.candidate.item)
        }

        return PageResponse(
            page = 0,
            size = books.size,
            totalPage = if (books.isEmpty()) 0 else 1,
            content = books
        )
    }

    private fun runPipeline(
        trace: RecommendationTrace,
        userId: Long,
        request: BookInfoRequest,
        readIsbns: Collection<String>
    ): RecommendationPipelineResult {
        var retryCount = 0

        val plannerResult = measureLatency(
            trace = trace,
            stage = "planner",
            latencyField = "plannerLatency",
            action = {
                plannerService.generateQueryCandidates(request)
            },
            endFields = { result ->
                mapOf(
                    "queryCount" to result.queries.size,
                    "promptTokens" to (result.promptTokenCount ?: 0),
                    "responseTokens" to (result.responseTokenCount ?: 0)
                )
            }
        )
        val primaryQueries = plannerResult.queries.ifEmpty { listOf(request.title) }
        trace.plannerQueries = primaryQueries

        var activeQueries = primaryQueries
        var retrievedCandidates = measureLatency(
            trace = trace,
            stage = "retriever",
            latencyField = "retrieverLatency",
            action = {
                retrieverService.retrieve(activeQueries)
            },
            endFields = { candidates ->
                mapOf("resultCount" to candidates.size)
            }
        )
        trace.retrieverResultCount = retrievedCandidates.size

        if (trace.retrieverResultCount < 3) {
            // 1차 검색 결과가 너무 적으면, 더 넓은 검색어 전략으로 Planner를 한 번만 재시도한다.
            retryCount = 1
            val retryPlannerResult = measureLatency(
                trace = trace,
                stage = "planner",
                latencyField = "plannerLatency",
                attempt = 2,
                action = {
                    plannerService.generateRelaxedQueryCandidates(request)
                },
                endFields = { result ->
                    mapOf(
                        "queryCount" to result.queries.size,
                        "promptTokens" to (result.promptTokenCount ?: 0),
                        "responseTokens" to (result.responseTokenCount ?: 0)
                    )
                }
            )
            val retryQueries = retryPlannerResult.queries.ifEmpty { primaryQueries }
            activeQueries = retryQueries
            trace.plannerQueries = retryQueries

            retrievedCandidates = measureLatency(
                trace = trace,
                stage = "retriever",
                latencyField = "retrieverLatency",
                attempt = 2,
                action = {
                    retrieverService.retrieve(retryQueries)
                },
                endFields = { candidates ->
                    mapOf("resultCount" to candidates.size)
                }
            )
            trace.retrieverResultCount = retrievedCandidates.size
        }

        val filterResult = measureLatency(
            trace = trace,
            stage = "filter",
            latencyField = "filterLatency",
            action = {
                filterService.filter(retrievedCandidates, readIsbns)
            },
            endFields = { result ->
                mapOf(
                    "resultCount" to result.candidates.size,
                    "removedCount" to result.removedCount
                )
            }
        )
        trace.filterRemovedCount = filterResult.removedCount

        val rankedRecommendations = measureLatency(
            trace = trace,
            stage = "ranker",
            latencyField = "rankerLatency",
            action = {
                rankerService.rank(request, filterResult.candidates)
            },
            endFields = { ranked ->
                mapOf("resultCount" to ranked.size)
            }
        )
        trace.rankedResultCount = rankedRecommendations.size

        val explainerResult = measureLatency(
            trace = trace,
            stage = "explainer",
            latencyField = "explainerLatency",
            action = {
                explainerService.explain(request, rankedRecommendations)
            },
            endFields = { result ->
                mapOf(
                    "resultCount" to result.explanationsByIsbn.size,
                    "promptTokens" to (result.promptTokenCount ?: 0),
                    "responseTokens" to (result.responseTokenCount ?: 0)
                )
            }
        )

        log.info(
            "recommendation.pipeline.end {}",
            entries(
                linkedMapOf(
                    "traceId" to trace.traceId,
                    "plannerQueries" to trace.plannerQueries,
                    "retrieverResultCount" to trace.retrieverResultCount,
                    "filterRemovedCount" to trace.filterRemovedCount,
                    "rankedResultCount" to trace.rankedResultCount,
                    "plannerLatency" to trace.plannerLatency,
                    "retrieverLatency" to trace.retrieverLatency,
                    "filterLatency" to trace.filterLatency,
                    "rankerLatency" to trace.rankerLatency,
                    "explainerLatency" to trace.explainerLatency,
                    "retryCount" to retryCount
                )
            )
        )

        return RecommendationPipelineResult(
            requestTitle = request.title,
            plannerQueries = trace.plannerQueries,
            plannerModel = plannerResult.model,
            plannerPromptTokenCount = plannerResult.promptTokenCount,
            plannerResponseTokenCount = plannerResult.responseTokenCount,
            plannerTotalTokenCount = plannerResult.totalTokenCount,
            explainerModel = explainerResult.model,
            explainerPromptTokenCount = explainerResult.promptTokenCount,
            explainerResponseTokenCount = explainerResult.responseTokenCount,
            explainerTotalTokenCount = explainerResult.totalTokenCount,
            retrieverResultCount = trace.retrieverResultCount,
            filterRemovedCount = trace.filterRemovedCount,
            rankedResultCount = trace.rankedResultCount,
            plannerLatency = trace.plannerLatency,
            retrieverLatency = trace.retrieverLatency,
            filterLatency = trace.filterLatency,
            rankerLatency = trace.rankerLatency,
            explainerLatency = trace.explainerLatency,
            retryCount = retryCount,
            rankedRecommendations = rankedRecommendations,
            explanationsByIsbn = explainerResult.explanationsByIsbn
        )
    }

    private fun <T> measureLatency(
        trace: RecommendationTrace,
        stage: String,
        latencyField: String,
        attempt: Int = 1,
        action: () -> T,
        endFields: (T) -> Map<String, Any> = { emptyMap() }
    ): T {
        logStage("${stage}.start", trace.traceId, mapOf("attempt" to attempt))
        val start = System.currentTimeMillis()
        try {
            val result = action()
            val latency = System.currentTimeMillis() - start
            accumulateLatency(trace, latencyField, latency)
            logStage(
                "${stage}.end",
                trace.traceId,
                linkedMapOf<String, Any>(
                    "attempt" to attempt,
                    "latencyMs" to latency
                ).apply { putAll(endFields(result)) }
            )
            return result
        } catch (ex: Exception) {
            val latency = System.currentTimeMillis() - start
            accumulateLatency(trace, latencyField, latency)
            logStage(
                "${stage}.end",
                trace.traceId,
                mapOf(
                    "attempt" to attempt,
                    "latencyMs" to latency,
                    "error" to true,
                    "exceptionClass" to ex::class.simpleName.orEmpty()
                )
            )
            throw ex
        }
    }

    private fun accumulateLatency(trace: RecommendationTrace, field: String, latency: Long) {
        when (field) {
            "plannerLatency" -> trace.plannerLatency += latency
            "retrieverLatency" -> trace.retrieverLatency += latency
            "filterLatency" -> trace.filterLatency += latency
            "rankerLatency" -> trace.rankerLatency += latency
            "explainerLatency" -> trace.explainerLatency += latency
        }
    }

    private fun logStage(event: String, traceId: String, extraFields: Map<String, Any>) {
        val logMap = linkedMapOf<String, Any>(
            "traceId" to traceId,
            "event" to event
        )
        logMap.putAll(extraFields)
        log.info(event + " {}", entries(logMap))
    }

}
