package com.beside.groubing.global.response.error

import com.beside.groubing.global.response.ApiResponseCode
import java.time.LocalDateTime

class ApiError(
    val code: ApiResponseCode,
    e: Throwable,
    val message: String = code.message
) {
    val timestamp: LocalDateTime = LocalDateTime.now()
    val debugMessage: String = e.toString()
}
