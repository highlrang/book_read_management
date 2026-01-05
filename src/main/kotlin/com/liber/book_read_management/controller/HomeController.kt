package com.liber.book_read_management.controller

import com.liber.book_read_management.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

// TODO
@RestController
@RequestMapping("/api/v1/user")
class HomeController (val userService: UserService) {
    @GetMapping
    fun home() : ResponseEntity<Unit?> {
        return ResponseEntity.ok(null)
    }
}