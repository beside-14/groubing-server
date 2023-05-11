package com.beside.groubing.groubingserver.domain.bingo.api

import com.beside.groubing.groubingserver.domain.bingo.application.BingoBoardCreateService
import com.beside.groubing.groubingserver.domain.bingo.payload.request.BingoBoardCreateRequest
import com.beside.groubing.groubingserver.domain.bingo.payload.response.BingoBoardResponse
import com.beside.groubing.groubingserver.global.response.ApiResponse
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/bingo-boards")
class BingoBoardCreateApi(
    private val bingoBoardCreateService: BingoBoardCreateService
) {
    @PostMapping
    fun create(
        @AuthenticationPrincipal
        memberId: Long,
        @RequestBody
        @Validated
        request: BingoBoardCreateRequest
    ): ApiResponse<BingoBoardResponse> {
        val command = request.command(memberId)
        val response = bingoBoardCreateService.create(command)
        return ApiResponse.OK(response)
    }
}
