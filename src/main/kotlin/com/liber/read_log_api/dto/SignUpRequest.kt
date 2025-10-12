package com.liber.read_log_api.dto

import com.liber.read_log_api.annotation.LoginId
import com.liber.read_log_api.annotation.Password
import com.liber.read_log_api.annotation.PhoneNumber
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

class SignUpRequest(
    @LoginId
    var loginId: String,
    @Password
    var password: String,
    @NotBlank
    @Size(max=10)
    var name: String,
    @PhoneNumber
    var phoneNumber: String,
    var address: String?,
    var addressDetail: String?,
    var addressLatitude: Double?,
    var addressLongitude: Double?
)