package com.liber.book_read_management.service.ai

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.genai.Client
import com.google.genai.types.GenerateContentConfig
import com.google.genai.types.Schema
import com.liber.book_read_management.dto.semantic.BookInfoRequest
import com.liber.book_read_management.dto.semantic.SemanticScore
import com.liber.book_read_management.dto.semantic.SemanticScoreSchema
import com.liber.book_read_management.exception.ApiException
import com.liber.book_read_management.exception.ExceptionType
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service

@Service
@RequiredArgsConstructor
class GenAIService(private var client: Client, private val objectMapper: ObjectMapper) {

    fun getBookSearchQuery(bookInfoRequest: BookInfoRequest): List<String> {

        val reviewsText = bookInfoRequest.reviews.joinToString("\n") { "- $it" }

        val prompt = """
            너는 도서 정보를 분석하는 평가기다.

            [abstraction]
            0.0 = 매우 구체적, 사례/행동 중심
            1.0 = 매우 추상적, 이론/철학 중심

            [emotion]
            0.0 = 감정 자극 거의 없음
            1.0 = 강한 감정 유발

            [challenge]
            0.0 = 읽기 쉽고 익숙함
            1.0 = 사고를 강하게 요구함

            [narrative]
            0.0 = 설명/분석 위주
            1.0 = 이야기/서사 위주

            [confidence]
            0.0 = 판단이 매우 불확실
            1.0 = 판단이 매우 확실

            아래 도서 정보를 분석하라:

            제목: ${bookInfoRequest.title}
            소개: ${bookInfoRequest.introduction}
            리뷰:
            $reviewsText

        """.trimIndent()

        val schemaJsonString = objectMapper.writeValueAsString(SemanticScoreSchema())
        val semanticScoreSchema = Schema.fromJson(schemaJsonString)

        val config = GenerateContentConfig.builder()
            .responseMimeType("application/json")
            .responseSchema(semanticScoreSchema)
            .build()

        val response = client.models.generateContent(
            "gemini-2.0-flash",
            prompt,
            config
        )

        val text = response.text()
            ?: throw ApiException(ExceptionType.INTERNAL_SERVER_ERROR)

        val semanticScore = objectMapper.readValue(text, SemanticScore::class.java)

        return generateAladdinQueries(semanticScore)
    }

    data class ArraySchema(
        val type: String = "array",
        val items: ItemSchema = ItemSchema()
    )

    data class ItemSchema(
        val type: String = "string"
    )

    fun generateAladdinQueries(score: SemanticScore): List<String> {
        val prompt = """
            사용자의 도서 취향 점수(0.0~1.0):
            - 추상성(abstraction): ${score.abstraction ?: "-"}
            - 감정성(emotion): ${score.emotion ?: "-"}
            - 도전성(challenge): ${score.challenge ?: "-"}
            - 서사성(narrative): ${score.narrative ?: "-"}
            - 확신도(confidence): ${score.confidence ?: "-"}

            이 점수에 가장 잘 어울리는 도서를 찾기 위한 한국어 검색 키워드 3개를 JSON 리스트 형식으로만 답변해줘.
            예: ["양자역학 전문 서적", "철학적 에세이", "현대 물리학 원리"]
        """.trimIndent()

        val schemaJson = objectMapper.writeValueAsString(ArraySchema())
        val responseSchema = Schema.fromJson(schemaJson)

        val config = GenerateContentConfig.builder()
            .responseMimeType("application/json")
            .responseSchema(responseSchema)
            .build()

        val response = client.models.generateContent(
            "gemini-2.0-flash",
            prompt,
            config
        )

        val text = response.text()
            ?: throw ApiException(ExceptionType.INTERNAL_SERVER_ERROR)

        return objectMapper.readValue(
            text,
            object : TypeReference<List<String>>() {}
        )

    }
}