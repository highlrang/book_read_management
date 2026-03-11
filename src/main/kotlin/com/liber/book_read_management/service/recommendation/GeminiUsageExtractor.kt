package com.liber.book_read_management.service.recommendation

import org.springframework.stereotype.Component

@Component
class GeminiUsageExtractor {

    fun extract(response: Any): GeminiTokenUsage {
        // Gemini API에서는 usage metadata가 안정적으로 제공되지 않을 수 있으므로,
        // reflection으로 억지 접근하지 않고 토큰 수를 비어 있는 값으로 처리한다.
        return GeminiTokenUsage()
    }
}

data class GeminiTokenUsage(
    val promptTokenCount: Int? = null,
    val responseTokenCount: Int? = null,
    val totalTokenCount: Int? = null
)
