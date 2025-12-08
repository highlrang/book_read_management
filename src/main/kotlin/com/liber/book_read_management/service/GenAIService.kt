package com.liber.book_read_management.service

import com.google.genai.Client
import com.google.genai.types.Content
import com.google.genai.types.GenerateContentConfig
import com.google.genai.types.GenerateContentResponse
import com.google.genai.types.Part
import com.google.genai.types.Schema
import com.google.genai.types.Type
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service
import java.util.List

@Service
@RequiredArgsConstructor
class GenAIService (private var client: Client) {

    fun recommendBook(responseSchema: Schema, messages: Content): String? {
        val config = GenerateContentConfig.builder()
            .responseMimeType("application/json")
            .responseSchema(responseSchema)
            .build()

        val res = client.models.generateContent("gemini-2.5-flash", messages, config)
        return res.text()
    }

    // TODO 동시성
    // SETNX seat:1234 userA | redisTemplate.opsForValue().setIfAbsent(key, value, duration);
    // 특정 수 이상의 트래픽을 대기 상태로 밀어냄

    // TODO 랭킹 sorted-set으로 순번 관리 redisTemplate.opsForZSet()
    // TODO 투표 -> 보상 이벤트(선착순 100명) INCR + SETNX

    // TODO RabbitMQ + SAGA 패턴
    // TODO WebSocket & Server Sent Events

    // TODO BookService로 이동
    fun makeBookRecommendContent() : Content {
        val readBooks = """
            모순
            아버지의 해방일지
        """.trimIndent()

        return Content.fromParts(
            Part.fromText("너는 도서 추천 어시스턴트야. 내가 읽은 책 기반으로 도서를 3개 추천해줘."),
            Part.fromText(readBooks)
        )
    }

    fun makeBookRecommendSchema() : Schema {
        val stringType = Schema.builder().type(Type.Known.STRING).build()
        val nestedBook = Schema.builder()
            .type(Type.Known.ARRAY)
            .items(
                Schema.builder()
                    .type(Type.Known.OBJECT)
                    .properties(
                        mapOf(
                            "title" to stringType,
                            "author" to stringType,
                            "reason" to stringType
                        )
                    )
                    .build()
            )
            .build()
        return Schema.builder()
            .type(Type.Known.OBJECT)
            .properties(
                mapOf(
                    "books" to nestedBook
                )
            )
            .build()
    }

}

/**
 * API Version.
 *
 * curl "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent" \
 *   -H "x-goog-api-key: $GEMINI_API_KEY" \
 *   -H 'Content-Type: application/json' \
 *   -X POST \
 *   -d '{
 *     "contents": [
 *       {
 *         "parts": [
 *           {
 *             "text": "Explain how AI works in a few words"
 *           }
 *         ]
 *       }
 *     ]
 *   }'
 *
 *   STREAMING 방식으로 응답 받을 수도 있음
 */