package com.liber.book_read_management.controller

import com.liber.book_read_management.service.GenAIService
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class IndexController (val genAIService: GenAIService) {

    @Value("\${NAME:GUEST}")
    private lateinit var NAME: String

    @GetMapping
    fun home() : ResponseEntity<String> {
        genAIService.recommendBook(genAIService.makeBookRecommendSchema(), genAIService.makeBookRecommendContent())
        return ResponseEntity.ok("Hello, $NAME!")
    }
}