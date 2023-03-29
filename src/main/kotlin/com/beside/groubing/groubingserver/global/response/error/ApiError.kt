package com.beside.groubing.groubingserver.global.response.error

import com.beside.groubing.groubingserver.global.response.ApiResponseCode
import java.time.LocalDateTime

class ApiError(
        val code: ApiResponseCode,
        e: Throwable
) {
    val message = code.message
    val timestamp = LocalDateTime.now()
    val debugMessage = e.toString()
}