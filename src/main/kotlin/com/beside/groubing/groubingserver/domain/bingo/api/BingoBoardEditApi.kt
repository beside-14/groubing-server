package com.beside.groubing.groubingserver.domain.bingo.api

import com.beside.groubing.groubingserver.domain.bingo.application.BingoBoardEditService
import com.beside.groubing.groubingserver.domain.bingo.payload.request.BingoBoardEditRequest
import com.beside.groubing.groubingserver.domain.bingo.payload.response.BingoBoardResponse
import com.beside.groubing.groubingserver.global.response.ApiResponse
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.BindException
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/bingos")
class BingoBoardEditApi(
    private val bingoBoardEditService: BingoBoardEditService
) {
    @PatchMapping("/boards")
    fun edit(
        @AuthenticationPrincipal
        memberId: Long,
        @RequestBody
        @Validated
        request: BingoBoardEditRequest,
        bindingResult: BindingResult
    ): ApiResponse<BingoBoardResponse> {
        if (bindingResult.hasErrors()) throw BindException(bindingResult)
        val command = request.command(memberId)
        val response = bingoBoardEditService.edit(command)
        return ApiResponse.OK(response)
    }
}
