package com.liber.book_read_management.service

import com.liber.book_read_management.dto.semantic.BookInfoRequest
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Service
@RequiredArgsConstructor
class SemanticServiceImpl(
    private val webClient: WebClient
) : SemanticService {
    override fun generateSemanticQuery(request: BookInfoRequest): List<String>? {
        return webClient.post()
            .uri("/api/generate")
            .body(Mono.just(request), BookInfoRequest::class.java)
            .retrieve()
            .bodyToMono(List::class.java)
            .block() as List<String>?
    }
}