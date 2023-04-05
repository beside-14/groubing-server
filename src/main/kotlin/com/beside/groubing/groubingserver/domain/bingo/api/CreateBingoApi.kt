package com.beside.groubing.groubingserver.domain.bingo.api

import com.beside.groubing.groubingserver.domain.bingo.application.CreateBingoService
import com.beside.groubing.groubingserver.domain.bingo.payload.request.CreateBingoRequest
import com.beside.groubing.groubingserver.domain.bingo.payload.response.BingoResponse
import com.beside.groubing.groubingserver.domain.member.domain.Member
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
class CreateBingoApi(
    private val createBingoService: CreateBingoService
) {

    @PostMapping
    fun create(
        @AuthenticationPrincipal member: Member,
        @RequestBody @Validated request: CreateBingoRequest,
        bindingResult: BindingResult
    ): ApiResponse<BingoResponse> {
        if (bindingResult.hasErrors()) throw BindException(bindingResult)
        val command = request.command(member.id)
        val response = createBingoService.create(command)
        return ApiResponse.OK(response)
    }
}
