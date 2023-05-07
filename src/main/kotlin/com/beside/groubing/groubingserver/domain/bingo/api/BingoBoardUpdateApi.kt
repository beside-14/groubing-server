package com.beside.groubing.groubingserver.domain.bingo.api

import com.beside.groubing.groubingserver.domain.bingo.application.BingoBoardUpdateService
import com.beside.groubing.groubingserver.domain.bingo.payload.request.BingoBoardBaseUpdateRequest
import com.beside.groubing.groubingserver.domain.bingo.payload.request.BingoBoardMembersPeriodUpdateRequest
import com.beside.groubing.groubingserver.domain.bingo.payload.request.BingoBoardMemoUpdateRequest
import com.beside.groubing.groubingserver.domain.bingo.payload.request.BingoBoardOpenUpdateRequest
import com.beside.groubing.groubingserver.domain.bingo.payload.response.BingoBoardResponse
import com.beside.groubing.groubingserver.global.response.ApiResponse
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/bingoboards")
class BingoBoardUpdateApi(
    private val bingoBoardUpdateService: BingoBoardUpdateService
) {
    @PatchMapping("/{id}/base")
    fun updateBase(
        @PathVariable id: Long,
        @AuthenticationPrincipal
        memberId: Long,
        @RequestBody @Valid
        baseUpdateRequest: BingoBoardBaseUpdateRequest
    ): ApiResponse<BingoBoardResponse> {
        val bingoBoardResponse = bingoBoardUpdateService.updateBase(id, memberId, baseUpdateRequest.command())
        return ApiResponse.OK(bingoBoardResponse)
    }

    @PatchMapping("/{id}/members-period")
    fun updateBingoMembersPeriod(
        @PathVariable id: Long,
        @AuthenticationPrincipal
        memberId: Long,
        @RequestBody @Valid
        memberPeriodUpdateRequest: BingoBoardMembersPeriodUpdateRequest
    ): ApiResponse<BingoBoardResponse>? {
        val bingoBoardResponse = bingoBoardUpdateService.updateMembersPeriod(id, memberId, memberPeriodUpdateRequest.command())
        return ApiResponse.OK(bingoBoardResponse)
    }

    @PatchMapping("/{id}/memo")
    fun updateMemo(
        @PathVariable id: Long,
        @AuthenticationPrincipal
        memberId: Long,
        @RequestBody @Valid
        memoUpdateRequest: BingoBoardMemoUpdateRequest
    ): ApiResponse<BingoBoardResponse> {
        val bingoBoardResponse = bingoBoardUpdateService.updateMemo(id, memberId, memoUpdateRequest.command())
        return ApiResponse.OK(bingoBoardResponse)
    }

    @PatchMapping("/{id}/open")
    fun updateOpen(
        @PathVariable id: Long,
        @AuthenticationPrincipal
        memberId: Long,
        @RequestBody @Valid
        openUpdateRequest: BingoBoardOpenUpdateRequest
    ): ApiResponse<BingoBoardResponse> {
        val bingoBoardResponse = bingoBoardUpdateService.updateOpen(id, memberId, openUpdateRequest.command())
        return ApiResponse.OK(bingoBoardResponse)
    }
}
