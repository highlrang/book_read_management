package com.liber.book_read_management.service.ai

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.liber.book_read_management.dto.semantic.BookInfoRequest
import com.liber.book_read_management.dto.semantic.SemanticScore
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

@Service
@RequiredArgsConstructor
class GenAIService (private var client: HttpClient) {

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

            반드시 아래 JSON 형식으로만 출력하라. 설명 문장은 포함하지 마라.

            {
              "abstraction": 0.0,
              "emotion": 0.0,
              "challenge": 0.0,
              "narrative": 0.0,
              "confidence": 0.0
            }

        """.trimIndent()

        val body = """
            {
              "model": "mistral",
              "stream": false,
              "prompt": ${ObjectMapper().writeValueAsString(prompt)}
            }

        """.trimIndent()

        val request: HttpRequest = HttpRequest.newBuilder()
            .uri(URI.create("http://host.docker.internal:11434Ï/api/generate"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(body))
            .build()

        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

        // 1차 파싱
        val root: JsonNode = ObjectMapper().readTree(response.body())
        val jsonText: String = root.get("response").asText()

        // 2차 파싱 (이게 진짜 의미 JSON)
        val semanticScore = ObjectMapper().readValue(jsonText, SemanticScore::class.java)

        return generateAladdinQueries(semanticScore)
    }

    fun generateAladdinQueries(score: SemanticScore): List<String> {
        val prompt = """
            사용자의 도서 취향 점수(0.0~1.0):
            - 추상성(abstraction): ${score.abstraction}
            - 감정성(emotion): ${score.emotion}
            - 도전성(challenge): ${score.challenge}
            - 서사성(narrative): ${score.narrative}
            - 확신도(confidence): ${score.confidence}

            이 점수에 가장 잘 어울리는 도서를 찾기 위한 한국어 검색 키워드 3개를 JSON 리스트 형식으로만 답변해줘.
            예: ["양자역학 전문 서적", "철학적 에세이", "현대 물리학 원리"]
        """.trimIndent()

        val request: HttpRequest = HttpRequest.newBuilder()
            .uri(URI.create("http://host.docker.internal:11434Ï/api/generate"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(prompt))
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return ObjectMapper().readValue(response.body(),  object : TypeReference<List<String>>() {})

    }
}