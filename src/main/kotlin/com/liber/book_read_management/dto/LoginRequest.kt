package com.liber.book_read_management.dto;

import com.liber.book_read_management.annotation.LoginId
import com.liber.book_read_management.annotation.Password

class LoginRequest (
    @LoginId
    var loginId: String,
    @Password
    var password: String
)