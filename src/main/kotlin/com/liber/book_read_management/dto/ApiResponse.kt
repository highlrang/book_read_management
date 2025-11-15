package com.liber.book_read_management.dto

class ApiResponse<T>(
    var status: String = "",
    var code: Int? = null,
    var message: String? = null,
    var data: T? = null
) {

    companion object {

        fun <T> success(): ApiResponse<T> {
            return ApiResponse<T>(
                status = "success"
            )
        }

        fun <T> success(data: T): ApiResponse<T> {
            return ApiResponse<T>(
                status = "success",
                data = data
            )
        }

        fun <T> fail(code: Int, message: String?): ApiResponse<T> {
            return ApiResponse<T>(
                status = "fail",
                code = code,
                message = message
            )
        }
    }
}