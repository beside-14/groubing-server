package com.beside.groubing.groubingserver.domain.bingo.api

import com.beside.groubing.groubingserver.domain.bingo.application.CreateBingoService
import com.beside.groubing.groubingserver.domain.bingo.payload.request.CreateBingoBoardRequest
import com.beside.groubing.groubingserver.domain.bingo.payload.response.BingoBoardResponse
import com.beside.groubing.groubingserver.global.response.ApiResponse
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.BindException
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class CreateBingoApi(
    private val createBingoService: CreateBingoService
) {

    @PostMapping("/api/bingos")
    fun create(
        @AuthenticationPrincipal
        memberId: Long,
        @RequestBody
        @Validated
        request: CreateBingoBoardRequest,
        bindingResult: BindingResult
    ): ApiResponse<BingoBoardResponse> {
        if (bindingResult.hasErrors()) throw BindException(bindingResult)
        val command = request.command(memberId)
        val response = createBingoService.create(command)
        return ApiResponse.OK(response)
    }
}
