package com.liber.read_log_api.exception

class ApiException(var exceptionType: ExceptionType, var customMessage: String?): RuntimeException() {

    constructor(exceptionType: ExceptionType) : this(exceptionType, null)

}