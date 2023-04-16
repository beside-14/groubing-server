package com.beside.groubing.groubingserver.domain.bingo.api

import com.beside.groubing.groubingserver.domain.bingo.application.BingoItemEditService
import com.beside.groubing.groubingserver.domain.bingo.payload.request.BingoItemEditRequest
import com.beside.groubing.groubingserver.domain.bingo.payload.response.BingoBoardResponse
import com.beside.groubing.groubingserver.global.response.ApiResponse
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/bingos/items")
class BingoItemEditApi(
    private val bingoItemEditService: BingoItemEditService
) {
    @PatchMapping
    fun edit(
        @AuthenticationPrincipal
        memberId: Long,
        @RequestBody
        @Validated
        request: BingoItemEditRequest
    ): ApiResponse<BingoBoardResponse.BingoItemResponse> {
        val command = request.command(memberId)
        val response = bingoItemEditService.edit(command)
        return ApiResponse.OK(response)
    }
}
