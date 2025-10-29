package com.liber.book_read_management.exception

class ApiException(var exceptionType: ExceptionType, var customMessage: String?): RuntimeException() {

    constructor(exceptionType: ExceptionType) : this(exceptionType, null)

}