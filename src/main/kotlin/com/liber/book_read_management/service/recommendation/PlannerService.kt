package com.liber.book_read_management.service.recommendation

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.genai.Client
import com.google.genai.types.GenerateContentConfig
import com.google.genai.types.Schema
import com.liber.book_read_management.config.RecommendationLlmProperties
import com.liber.book_read_management.dto.semantic.BookInfoRequest
import com.liber.book_read_management.exception.ApiException
import com.liber.book_read_management.exception.ExceptionType
import com.liber.book_read_management.service.recommendation.model.PlannerResult
import org.springframework.stereotype.Service
import kotlin.math.max

@Service
class PlannerService(
    private val client: Client,
    private val objectMapper: ObjectMapper,
    private val recommendationLlmProperties: RecommendationLlmProperties,
    private val geminiUsageExtractor: GeminiUsageExtractor,
    private val promptRepository: PromptRepository,
    private val promptTemplateFormatter: PromptTemplateFormatter
) {
    fun generateQueryCandidates(bookInfoRequest: BookInfoRequest): PlannerResult {
        return generate(bookInfoRequest, PlannerStrategy.DEFAULT)
    }

    fun generateRelaxedQueryCandidates(bookInfoRequest: BookInfoRequest): PlannerResult {
        return generate(bookInfoRequest, PlannerStrategy.RELAXED)
    }

    private fun formatReviews(reviews: List<String>): String {
        return reviews
            .filter { it.isNotBlank() }
            .joinToString("\n") { "- $it" }
            .ifBlank { "- 리뷰 없음" }
    }

    private fun generate(bookInfoRequest: BookInfoRequest, strategy: PlannerStrategy): PlannerResult {
        val model = recommendationLlmProperties.plannerModel
        val prompt = promptTemplateFormatter.format(
            buildPrompt(strategy),
            mapOf(
                "title" to bookInfoRequest.title,
                "introduction" to bookInfoRequest.introduction,
                "reviews" to formatReviews(bookInfoRequest.reviews)
            )
        )

        val config = GenerateContentConfig.builder()
            .responseMimeType("application/json")
            .responseSchema(Schema.fromJson(objectMapper.writeValueAsString(ArraySchema())))
            .build()

        val response = client.models.generateContent(
            model,
            prompt,
            config
        )
        val rawResponse = response.text() ?: throw ApiException(ExceptionType.INTERNAL_SERVER_ERROR)
        val usage = geminiUsageExtractor.extract(response)

        val queryCandidates = objectMapper.readValue(
            rawResponse,
            object : TypeReference<List<String>>() {}
        )

        return PlannerResult(
            model = model,
            promptTokenCount = usage.promptTokenCount,
            responseTokenCount = usage.responseTokenCount,
            totalTokenCount = usage.totalTokenCount,
            queries = sanitizeQueries(queryCandidates, bookInfoRequest)
        )
    }

    private fun buildPrompt(strategy: PlannerStrategy): String {
        val basePrompt = promptRepository.getPrompt(PromptType.BOOK_QUERY_PLANNER)
        if (strategy == PlannerStrategy.DEFAULT) {
            return basePrompt
        }

        return "$basePrompt\n\n추가 지침: 결과가 너무 적게 나올 수 있으니 저자명, 장르명, 주제어처럼 더 넓은 탐색용 검색어를 우선하라."
    }

    private fun sanitizeQueries(rawQueries: List<String>, bookInfoRequest: BookInfoRequest): List<String> {
        // 거의 같은 검색어는 하나로 정리해서, Retriever가 비슷한 변형어에 호출을 낭비하지 않도록 한다.
        val distinctQueries = rawQueries
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .fold(mutableListOf<String>()) { acc, query ->
                if (acc.none { isSimilar(it, query) }) {
                    acc += query
                }
                acc
            }
            .toMutableList()

        if (distinctQueries.size < 3) {
            // 모델이 충분히 다양한 검색어를 못 줘도 Planner의 3~5개 계약은 유지한다.
            buildFallbackQueries(bookInfoRequest).forEach { fallback ->
                if (distinctQueries.size >= 5) {
                    return@forEach
                }
                if (distinctQueries.none { isSimilar(it, fallback) }) {
                    distinctQueries += fallback
                }
            }
        }

        return distinctQueries.take(max(3, distinctQueries.size.coerceAtMost(5)))
    }

    private fun buildFallbackQueries(bookInfoRequest: BookInfoRequest): List<String> {
        val title = bookInfoRequest.title.trim()
        val introTokens = bookInfoRequest.introduction
            .split(Regex("[|/]"))
            .map { it.trim() }
            .filter { it.isNotBlank() }

        return listOf(title) + introTokens + listOf("$title 추천", "$title 비슷한 책")
    }

    private fun isSimilar(left: String, right: String): Boolean {
        val leftNormalized = normalize(left)
        val rightNormalized = normalize(right)
        if (leftNormalized == rightNormalized) {
            return true
        }
        if (leftNormalized.contains(rightNormalized) || rightNormalized.contains(leftNormalized)) {
            return true
        }

        val leftTokens = leftNormalized.split(" ").filter { it.isNotBlank() }.toSet()
        val rightTokens = rightNormalized.split(" ").filter { it.isNotBlank() }.toSet()
        if (leftTokens.isEmpty() || rightTokens.isEmpty()) {
            return false
        }

        val intersection = leftTokens.intersect(rightTokens).size.toDouble()
        val union = leftTokens.union(rightTokens).size.toDouble()
        return union > 0 && (intersection / union) >= 0.7
    }

    private fun normalize(value: String): String {
        return value.lowercase()
            .replace(Regex("\\s+"), " ")
            .trim()
    }

    private data class ArraySchema(
        val type: String = "array",
        val minItems: Int = 3,
        val maxItems: Int = 5,
        val items: ItemSchema = ItemSchema()
    )

    private data class ItemSchema(
        val type: String = "string"
    )
}
