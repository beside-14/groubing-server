package com.beside.groubing.groubingserver.domain.bingo.api

import com.beside.groubing.groubingserver.domain.bingo.application.BingoBoardEditService
import com.beside.groubing.groubingserver.domain.bingo.payload.request.BingoBoardEditRequest
import com.beside.groubing.groubingserver.domain.bingo.payload.request.BingoBoardMemoEditRequest
import com.beside.groubing.groubingserver.domain.bingo.payload.request.BingoBoardOpenableEditRequest
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
@RequestMapping("/api/bingoboards")
class BingoBoardEditApi(
    private val bingoBoardEditService: BingoBoardEditService
) {
    @PatchMapping
    fun editBoard(
        @AuthenticationPrincipal
        memberId: Long,
        @RequestBody
        @Validated
        request: BingoBoardEditRequest,
        bindingResult: BindingResult
    ): ApiResponse<BingoBoardResponse> {
        if (bindingResult.hasErrors()) throw BindException(bindingResult)
        val command = request.command(memberId)
        val response = bingoBoardEditService.editBoard(command)
        return ApiResponse.OK(response)
    }

    @PatchMapping("/memo")
    fun editMemo(
        @AuthenticationPrincipal
        memberId: Long,
        @RequestBody
        @Validated
        request: BingoBoardMemoEditRequest,
        bindingResult: BindingResult
    ): ApiResponse<BingoBoardResponse> {
        if (bindingResult.hasErrors()) throw BindException(bindingResult)
        val response = bingoBoardEditService.editMemo(memberId, request.id, request.memo)
        return ApiResponse.OK(response)
    }

    @PatchMapping("/open")
    fun editOpenable(
        @AuthenticationPrincipal
        memberId: Long,
        @RequestBody
        @Validated
        request: BingoBoardOpenableEditRequest,
        bindingResult: BindingResult
    ): ApiResponse<BingoBoardResponse> {
        if (bindingResult.hasErrors()) throw BindException(bindingResult)
        val response = bingoBoardEditService.editOpenable(memberId, request.id, request.open)
        return ApiResponse.OK(response)
    }
}
