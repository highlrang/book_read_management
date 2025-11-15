package com.liber.book_read_management.dto

import com.liber.book_read_management.annotation.LoginId
import com.liber.book_read_management.annotation.Password
import com.liber.book_read_management.annotation.PhoneNumber
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