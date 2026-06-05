package com.beside.groubing.domain.bingo.api

import com.beside.groubing.domain.bingo.application.BingoBoardUpdateService
import com.beside.groubing.domain.bingo.payload.request.BingoBoardBaseUpdateRequest
import com.beside.groubing.domain.bingo.payload.request.BingoBoardMembersPeriodUpdateRequest
import com.beside.groubing.domain.bingo.payload.request.BingoBoardMemoUpdateRequest
import com.beside.groubing.domain.bingo.payload.request.BingoBoardOpenUpdateRequest
import com.beside.groubing.domain.bingo.payload.response.BingoBoardBaseUpdateResponse
import com.beside.groubing.domain.bingo.payload.response.BingoBoardMembersPeriodUpdateResponse
import com.beside.groubing.domain.bingo.payload.response.BingoBoardMemoUpdateResponse
import com.beside.groubing.domain.bingo.payload.response.BingoBoardOpenUpdateResponse
import com.beside.groubing.global.domain.id.ObfuscationType
import com.beside.groubing.global.id.DecryptId
import com.beside.groubing.global.response.ApiResponse
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/bingo-boards")
class BingoBoardUpdateApi(
    private val bingoBoardUpdateService: BingoBoardUpdateService
) {
    @PatchMapping("/{id}/base")
    fun updateBase(
        @PathVariable @DecryptId(ObfuscationType.BINGO_BOARD) id: Long,
        @AuthenticationPrincipal
        memberId: Long,
        @RequestBody @Valid
        baseUpdateRequest: BingoBoardBaseUpdateRequest
    ): ApiResponse<BingoBoardBaseUpdateResponse> {
        val updated = bingoBoardUpdateService.updateBase(id, memberId, baseUpdateRequest.command())
        return ApiResponse.OK(BingoBoardBaseUpdateResponse.fromBingoBoard(updated))
    }

    @PatchMapping("/{id}/publish-info")
    fun updateBingoMembersPeriod(
        @PathVariable @DecryptId(ObfuscationType.BINGO_BOARD) id: Long,
        @AuthenticationPrincipal
        memberId: Long,
        @RequestBody @Valid
        memberPeriodUpdateRequest: BingoBoardMembersPeriodUpdateRequest
    ): ApiResponse<BingoBoardMembersPeriodUpdateResponse> {
        val updated = bingoBoardUpdateService.updateMembersPeriod(id, memberId, memberPeriodUpdateRequest.command())
        return ApiResponse.OK(BingoBoardMembersPeriodUpdateResponse.fromBingoBoard(updated))
    }

    @PatchMapping("/{id}/memo")
    fun updateMemo(
        @PathVariable @DecryptId(ObfuscationType.BINGO_BOARD) id: Long,
        @AuthenticationPrincipal
        memberId: Long,
        @RequestBody @Valid
        memoUpdateRequest: BingoBoardMemoUpdateRequest
    ): ApiResponse<BingoBoardMemoUpdateResponse> {
        val updated = bingoBoardUpdateService.updateMemo(id, memberId, memoUpdateRequest.command())
        return ApiResponse.OK(BingoBoardMemoUpdateResponse.fromBingoBoard(updated))
    }

    @PatchMapping("/{id}/open")
    fun updateOpen(
        @PathVariable @DecryptId(ObfuscationType.BINGO_BOARD) id: Long,
        @AuthenticationPrincipal
        memberId: Long,
        @RequestBody @Valid
        openUpdateRequest: BingoBoardOpenUpdateRequest
    ): ApiResponse<BingoBoardOpenUpdateResponse> {
        val updated = bingoBoardUpdateService.updateOpen(id, memberId, openUpdateRequest.command())
        return ApiResponse.OK(BingoBoardOpenUpdateResponse.fromBingoBoard(updated))
    }
}
