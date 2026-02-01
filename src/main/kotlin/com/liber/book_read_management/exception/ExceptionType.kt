package com.liber.book_read_management.exception

enum class ExceptionType(val code: Int, val message: String) {
    INVALID_AUTH(1000, "유효하지 않은 인증입니다."),
    PASSWORD_NOT_MATCHED(1001, "비밀번호가 일치하지 않습니다."),

    DATA_NOT_FOUND(2000, "데이터를 찾을 수 없습니다"),
    ALREADY_EXIST(2001, "이미 존재하는 데이터입니다"),
    VALIDATION_ERROR(2002, "검증에 실패했습니다."),

    INTERNAL_SERVER_ERROR(5000, "서버 오류가 발생했습니다.")
    ;
}