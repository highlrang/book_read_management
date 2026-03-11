package com.liber.book_read_management.service.recommendation

import com.liber.book_read_management.dto.BookSearchResponse
import com.liber.book_read_management.dto.PageResponse
import com.liber.book_read_management.dto.semantic.BookInfoRequest
import com.liber.book_read_management.service.recommendation.model.RecommendationTrace
import mu.KotlinLogging
import net.logstash.logback.argument.StructuredArguments.entries
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import java.util.UUID

private val log = KotlinLogging.logger {}

/*
 * 도서 추천 오케스트레이션
 * PlannerService -> RetrieverService -> FilterService -> RankerService -> ExplainerService 순서로 동작
 */
@Service
class RecommendationAgentService(
    private val plannerService: PlannerService,
    private val retrieverService: RetrieverService,
    private val filterService: FilterService,
    private val rankerService: RankerService,
    private val explainerService: ExplainerService
) {

    @Cacheable(
        cacheNames = ["recommendations"],
        key = "T(com.liber.book_read_management.service.recommendation.RecommendationCacheKeyGenerator).generate(#userId, #request)"
    )
    fun recommend(userId: Long, request: BookInfoRequest): PageResponse<List<BookSearchResponse>> {
        val trace = RecommendationTrace(traceId = UUID.randomUUID().toString())
        var retryCount = 0

        val primaryQueries = measureLatency(
            trace = trace,
            stage = "planner",
            latencyField = "plannerLatency",
            action = {
                plannerService.generateQueryCandidates(request).ifEmpty { listOf(request.title) }
            },
            endFields = { queries ->
                mapOf("queryCount" to queries.size)
            }
        )
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
            val retryQueries = measureLatency(
                trace = trace,
                stage = "planner",
                latencyField = "plannerLatency",
                attempt = 2,
                action = {
                    plannerService.generateRelaxedQueryCandidates(request).ifEmpty { primaryQueries }
                },
                endFields = { queries ->
                    mapOf("queryCount" to queries.size)
                }
            )
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
                filterService.filter(userId, retrievedCandidates)
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

        val reasonsByIsbn = measureLatency(
            trace = trace,
            stage = "explainer",
            latencyField = "explainerLatency",
            action = {
                explainerService.explain(request, rankedRecommendations)
            },
            endFields = { explanations ->
                mapOf("resultCount" to explanations.size)
            }
        )

        val books = rankedRecommendations.map { ranked ->
            val item = ranked.candidate.item
            BookSearchResponse.of(item)
        }

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

        return PageResponse(
            page = 0,
            size = books.size,
            totalPage = if (books.isEmpty()) 0 else 1,
            content = books
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
