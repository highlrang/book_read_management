package com.liber.book_read_management.controller

import com.liber.book_read_management.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

// 추후 사용자 홈 API 확장 시 활성화 예정
@RestController
//@RequestMapping("/api/v1/user")
class HomeController (val userService: UserService) {

}
