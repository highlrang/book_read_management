package com.liber.read_log_api.dto;

import com.liber.read_log_api.annotation.LoginId
import com.liber.read_log_api.annotation.Password

class LoginRequest (
    @LoginId
    var loginId: String,
    @Password
    var password: String
)