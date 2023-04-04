package com.beside.groubing.groubingserver.global.response

class ApiResponse<T> private constructor(
    val data: T,
    val code: ApiResponseCode
) {
    companion object Factory {
        fun <T> OK(data: T): ApiResponse<T> {
            return ApiResponse(data, ApiResponseCode.OK)
        }
    }

    val message = code.message
}
