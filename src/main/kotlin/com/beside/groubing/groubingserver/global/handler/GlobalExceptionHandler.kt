package com.beside.groubing.groubingserver.global.handler

import com.beside.groubing.groubingserver.domain.bingo.exception.BingoInputException
import com.beside.groubing.groubingserver.domain.blockedmember.exception.BlockedMemberInputException
import com.beside.groubing.groubingserver.domain.friend.exception.FriendInputException
import com.beside.groubing.groubingserver.domain.member.exception.MemberInputException
import com.beside.groubing.groubingserver.global.domain.file.exception.FileInfoInputException
import com.beside.groubing.groubingserver.global.response.ApiResponseCode
import com.beside.groubing.groubingserver.global.response.error.ApiError
import org.hibernate.exception.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingRequestHeaderException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(MissingRequestHeaderException::class)
    fun handle(e: MissingRequestHeaderException): ResponseEntity<ApiError> {
        val apiError = ApiError(ApiResponseCode.MISSING_REQUEST_HEADER, e)
        return ResponseEntity(apiError, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handle(e: MissingServletRequestParameterException): ResponseEntity<ApiError> {
        val apiError = ApiError(ApiResponseCode.MISSING_REQUEST_PARAMETER, e)
        return ResponseEntity(apiError, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handle(e: HttpMessageNotReadableException): ResponseEntity<ApiError> {
        val apiError = ApiError(ApiResponseCode.NOT_READABLE_REQUEST_BODY, e)
        return ResponseEntity(apiError, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handle(e: ConstraintViolationException): ResponseEntity<ApiError> {
        val apiError = ApiError(ApiResponseCode.BAD_REQUEST_HEADER, e)
        return ResponseEntity(apiError, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(BindException::class)
    fun handle(e: BindException): ResponseEntity<ApiError> {
        val code = ApiResponseCode.BAD_REQUEST_BODY
        val apiError = ApiError(code, e, e.fieldError?.defaultMessage ?: code.message)
        return ResponseEntity(apiError, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handle(e: MethodArgumentNotValidException): ResponseEntity<ApiError> {
        val apiError = ApiError(ApiResponseCode.BAD_REQUEST_BODY, e)
        return ResponseEntity(apiError, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MemberInputException::class)
    fun handle(e: MemberInputException): ResponseEntity<ApiError> {
        val apiError = ApiError(ApiResponseCode.BAD_MEMBER_INPUT, e)
        return ResponseEntity(apiError, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(BingoInputException::class)
    fun handle(e: BingoInputException): ResponseEntity<ApiError> {
        val apiError = ApiError(ApiResponseCode.BAD_MEMBER_INPUT, e)
        return ResponseEntity(apiError, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(FileInfoInputException::class)
    fun handle(e: FileInfoInputException): ResponseEntity<ApiError> {
        val apiError = ApiError(ApiResponseCode.BAD_MEMBER_INPUT, e)
        return ResponseEntity(apiError, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(FriendInputException::class)
    fun handle(e: FriendInputException): ResponseEntity<ApiError> {
        val apiError = ApiError(ApiResponseCode.BAD_MEMBER_INPUT, e)
        return ResponseEntity(apiError, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(BlockedMemberInputException::class)
    fun handle(e: BlockedMemberInputException): ResponseEntity<ApiError> {
        val apiError = ApiError(ApiResponseCode.BAD_MEMBER_INPUT, e)
        return ResponseEntity(apiError, HttpStatus.BAD_REQUEST)
    }
}
