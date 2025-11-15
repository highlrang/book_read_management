package com.liber.book_read_management.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HomeController {

    @Value("\${NAME:GUEST}")
    private lateinit var NAME: String

    @GetMapping
    fun home() : ResponseEntity<String> {
        return ResponseEntity.ok("Hello, $NAME!")
    }
}