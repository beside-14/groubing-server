package com.beside.groubing.groubingserver.extension

import com.beside.groubing.groubingserver.global.response.ApiResponseCode
import com.beside.groubing.groubingserver.global.response.error.ApiError
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

fun ApiResponseCode.response(status: HttpStatus, e: Exception): ResponseEntity<ApiError> {
    val apiError = ApiError(this, e)
    return ResponseEntity(apiError, status)
}
