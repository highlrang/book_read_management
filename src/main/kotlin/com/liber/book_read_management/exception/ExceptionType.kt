package com.liber.book_read_management.exception

enum class ExceptionType(val code: Int, val message: String) {
    INVALID_AUTH(1000, "유효하지 않은 인증입니다."),
    PASSWORD_NOT_MATCHED(1001, "비밀번호가 일치하지 않습니다."),
    SOCIAL_LOGIN_DISABLED(1002, "현재 사용할 수 없는 소셜 로그인입니다."),

    DATA_NOT_FOUND(2000, "데이터를 찾을 수 없습니다"),
    ALREADY_EXIST(2001, "이미 존재하는 데이터입니다"),
    VALIDATION_ERROR(2002, "검증에 실패했습니다."),
    INVALID_EMAIL_VERIFICATION_TOKEN(2003, "유효하지 않은 인증 토큰입니다."),
    EXPIRED_EMAIL_VERIFICATION_TOKEN(2004, "만료된 인증 토큰입니다."),

    INTERNAL_SERVER_ERROR(5000, "서버 오류가 발생했습니다.")
    ;
}
