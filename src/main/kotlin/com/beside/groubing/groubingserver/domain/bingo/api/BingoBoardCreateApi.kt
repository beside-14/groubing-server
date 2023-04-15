package com.beside.groubing.groubingserver.domain.bingo.api

import com.beside.groubing.groubingserver.domain.bingo.application.BingoBoardCreateService
import com.beside.groubing.groubingserver.domain.bingo.payload.request.BingoBoardCreateRequest
import com.beside.groubing.groubingserver.domain.bingo.payload.response.BingoBoardResponse
import com.beside.groubing.groubingserver.domain.bingo.validator.BingoBoardCreateValidator
import com.beside.groubing.groubingserver.global.response.ApiResponse
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.BindException
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/bingos")
class BingoBoardCreateApi(
    private val bingoBoardCreateService: BingoBoardCreateService,
    private val bingoBoardCreateValidator: BingoBoardCreateValidator
) {
    @PostMapping("/boards")
    fun create(
        @AuthenticationPrincipal
        memberId: Long,
        @RequestBody
        @Validated
        request: BingoBoardCreateRequest,
        bindingResult: BindingResult
    ): ApiResponse<BingoBoardResponse> {
        bingoBoardCreateValidator.validate(request, bindingResult)
        if (bindingResult.hasErrors()) throw BindException(bindingResult)
        val command = request.command(memberId)
        val response = bingoBoardCreateService.create(command)
        return ApiResponse.OK(response)
    }
}
