package com.liber.read_log_api.dto

import io.swagger.v3.oas.annotations.media.Schema

class SignUpRequest(
    @Schema(example = "hwhw1234") // TODO schema & validation
    var loginId: String,
    var password: String,
    var name: String,
    var address: String?,
    var addressDetail: String?,
    var addressLatitude: Double?,
    var addressLongitude: Double?
)