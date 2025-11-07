package com.liber.book_read_management.service

import com.google.genai.Client
import com.google.genai.types.Content
import com.google.genai.types.GenerateContentConfig
import com.google.genai.types.GenerateContentResponse
import com.google.genai.types.Part
import com.google.genai.types.Schema
import com.google.genai.types.Type
import lombok.RequiredArgsConstructor
import java.util.List

@RequiredArgsConstructor
class GenAIService (private var client: Client) {

//    “한국+해외 도서를 함께 다루려면, AI는 ‘추천 로직 담당’,
//    도서 API는 ‘데이터 신뢰성 보완’으로 역할 분리

    // TODO Gemini API 연동
    // start chat == session
    // get history
    // id 확인

    // TODO ChatGPT API 연동
    // session
    // response api

    // TODO 알라딘 API 연동

    fun temp2() {

        val stringType = Schema.builder().type(Type.Known.STRING).build()

        val nestedBook = Schema.builder()
            .type(Type.Known.OBJECT)
            .properties(
                mapOf(
                    "title" to stringType,
                    "author" to stringType,
                    "reason" to stringType
                )
            )
            .build()

        val config = GenerateContentConfig.builder()
            .responseMimeType("application/json")
            .responseSchema(
                Schema.builder()
                    .type(Type.Known.OBJECT)
                    .properties(
                        mapOf(
                            "books" to nestedBook
                        )
                    )
                    .build()
            )
            .build()

        val requestContent = """
            내가 읽은 책 목록은 다음과 같아.
            1. 모순
            2. 아버지의 해방일지
            이 소설책들이랑 비슷한 국내 소설 책 추천해줘.
        """.trimIndent()

        val fromParts : Content = Content.fromParts(Part.fromText("너는 도서 추천 어시스턴트야."))
        fromParts.toBuilder().role("user").build()

        val messages: MutableList<Content?>? = List.of(
            Content.fromParts(Part.fromText("너는 도서 추천 어시스턴트야.")),
            Content.fromParts(Part.fromText("감성적인 소설 한 권 추천해줘.")),
            Content.fromParts(Part.fromText("책 표지를 보여줄게.")),
// Part.fromMimeTypeAndData("image/png", Files.readAllBytes(Paths.get("cover.png"))))
        )

        val client = Client()
        val res = client.models.generateContent("gemini-1.5-pro", requestContent, config)

        System.out.println(res.text())
    }

    fun temp() {
        val response: GenerateContentResponse =
            client.models.generateContent(
                "gemini-2.5-flash",
                "ㅇㅇㅇ",
                null
            )

        println(response.text())
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