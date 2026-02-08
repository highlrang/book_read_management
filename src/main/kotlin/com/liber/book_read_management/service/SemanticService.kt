package com.liber.book_read_management.service

import com.liber.book_read_management.dto.semantic.BookInfoRequest
import com.liber.book_read_management.dto.semantic.SemanticScore
import reactor.core.publisher.Mono

interface SemanticService {
    fun generateSemanticScore(request: BookInfoRequest): Mono<SemanticScore>
}