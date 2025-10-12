package com.liber.read_log_api.exception

enum class ExceptionType(val code: Int, val message: String) {
    INVALID_AUTH(1000, "유효하지 않은 인증입니다."),
    DATA_NOT_FOUND(2000, "데이터를 찾을 수 없습니다");
}