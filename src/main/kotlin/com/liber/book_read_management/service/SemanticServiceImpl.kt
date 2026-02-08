package com.liber.book_read_management.service

import com.liber.book_read_management.dto.semantic.BookInfoRequest
import com.liber.book_read_management.service.ai.GenAIService
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
@RequiredArgsConstructor
class SemanticServiceImpl(
    private val genAIService: GenAIService
) : SemanticService {
    override fun generateSemanticQuery(request: BookInfoRequest): List<String>? {
        return genAIService.getBookSearchQuery(request)
    }
}