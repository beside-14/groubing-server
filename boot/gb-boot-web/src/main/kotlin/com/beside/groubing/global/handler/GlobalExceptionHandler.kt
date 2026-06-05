package com.beside.groubing.global.handler

import com.beside.groubing.domain.bingo.exception.BingoInputException
import com.beside.groubing.domain.blockedmember.exception.BlockedMemberInputException
import com.beside.groubing.domain.friend.exception.FriendInputException
import com.beside.groubing.domain.member.exception.MemberInputException
import com.beside.groubing.global.domain.file.exception.FileInfoInputException
import com.beside.groubing.global.domain.id.exception.InvalidObfuscatedIdException
import com.beside.groubing.global.response.ApiResponseCode
import com.beside.groubing.global.response.error.ApiError
import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
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

    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

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

    @ExceptionHandler(DataIntegrityViolationException::class)
    fun handle(e: DataIntegrityViolationException): ResponseEntity<ApiError> {
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

    @ExceptionHandler(InvalidObfuscatedIdException::class)
    fun handle(e: InvalidObfuscatedIdException): ResponseEntity<ApiError> {
        val apiError = ApiError(ApiResponseCode.BAD_PARAMETER, e)
        return ResponseEntity(apiError, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ApiError> {
        log.error("handleException", e)
        val apiError = ApiError(ApiResponseCode.SERVER_ERROR, e)
        return ResponseEntity(apiError, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
