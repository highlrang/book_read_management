package com.liber.book_read_management.service.recommendation

import org.springframework.stereotype.Component

@Component
class GeminiUsageExtractor {

    fun extract(response: Any): GeminiTokenUsage {
        // Java GenAI javadoc states GenerateContentResponseUsageMetadata is not supported in Gemini API.
        // This service uses Gemini directly, so token counts are treated as unavailable instead of using reflection.
        return GeminiTokenUsage()
    }
}

data class GeminiTokenUsage(
    val promptTokenCount: Int? = null,
    val responseTokenCount: Int? = null,
    val totalTokenCount: Int? = null
)
