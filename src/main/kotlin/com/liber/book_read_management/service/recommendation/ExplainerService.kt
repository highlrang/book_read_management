package com.liber.book_read_management.service.recommendation

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.genai.Client
import com.google.genai.types.GenerateContentConfig
import com.google.genai.types.Schema
import com.liber.book_read_management.config.RecommendationLlmProperties
import com.liber.book_read_management.dto.semantic.BookInfoRequest
import com.liber.book_read_management.dto.semantic.RecommendationExplanation
import com.liber.book_read_management.exception.ApiException
import com.liber.book_read_management.exception.ExceptionType
import com.liber.book_read_management.service.recommendation.model.RankedRecommendation
import org.springframework.stereotype.Service

@Service
class ExplainerService(
    private val client: Client,
    private val objectMapper: ObjectMapper,
    private val recommendationLlmProperties: RecommendationLlmProperties,
    private val promptRepository: PromptRepository,
    private val promptTemplateFormatter: PromptTemplateFormatter
) {

    fun explain(bookInfoRequest: BookInfoRequest, rankedRecommendations: List<RankedRecommendation>): Map<String, String> {
        if (rankedRecommendations.isEmpty()) {
            return emptyMap()
        }

        val prompt = promptTemplateFormatter.format(
            promptRepository.getPrompt(PromptType.BOOK_RECOMMENDATION_EXPLAINER),
            mapOf(
                "title" to bookInfoRequest.title,
                "introduction" to bookInfoRequest.introduction,
                "reviews" to formatReviews(bookInfoRequest.reviews),
                "books" to formatBooks(rankedRecommendations)
            )
        )

        val config = GenerateContentConfig.builder()
            .responseMimeType("application/json")
            .responseSchema(Schema.fromJson(objectMapper.writeValueAsString(ExplanationArraySchema())))
            .build()

        val response = client.models.generateContent(
            recommendationLlmProperties.explainerModel,
            prompt,
            config
        )

        val explanations = objectMapper.readValue(
            response.text() ?: throw ApiException(ExceptionType.INTERNAL_SERVER_ERROR),
            object : TypeReference<List<RecommendationExplanation>>() {}
        )

        return explanations.associate { it.isbn to it.reason }
    }

    private fun formatReviews(reviews: List<String>): String {
        return reviews
            .filter { it.isNotBlank() }
            .joinToString("\n") { "- $it" }
            .ifBlank { "- 리뷰 없음" }
    }

    private fun formatBooks(rankedRecommendations: List<RankedRecommendation>): String {
        return rankedRecommendations.joinToString("\n\n") { ranked ->
            val item = ranked.candidate.item
            """
            ISBN: ${item.isbn13 ?: item.isbn.orEmpty()}
            제목: ${item.title.orEmpty()}
            저자: ${item.author.orEmpty()}
            설명: ${item.description.orEmpty()}
            출간일: ${item.pubDate.orEmpty()}
            카테고리: ${item.categoryName.orEmpty()}
            """.trimIndent()
        }
    }

    private data class ExplanationArraySchema(
        val type: String = "array",
        val items: ExplanationItemSchema = ExplanationItemSchema()
    )

    private data class ExplanationItemSchema(
        val type: String = "object",
        val properties: Map<String, PropertySchema> = mapOf(
            "isbn" to PropertySchema(description = "추천 도서 ISBN"),
            "reason" to PropertySchema(description = "1~2문장 추천 이유")
        ),
        val required: List<String> = listOf("isbn", "reason")
    )

    private data class PropertySchema(
        val type: String = "string",
        val description: String
    )
}
