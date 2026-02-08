package com.liber.book_read_management.service

import com.liber.book_read_management.dto.semantic.BookInfoRequest

interface SemanticService {
    fun generateSemanticQuery(request: BookInfoRequest): List<String>?
}