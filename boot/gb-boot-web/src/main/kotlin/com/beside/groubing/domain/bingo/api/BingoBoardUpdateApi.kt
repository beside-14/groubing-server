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
import com.beside.groubing.domain.common.id.ObfuscationType
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
    @PatchMapping("/{bingoBoardId}/base")
    fun updateBase(
        @PathVariable @DecryptId(ObfuscationType.BINGO_BOARD) bingoBoardId: Long,
        @AuthenticationPrincipal
        memberId: Long,
        @RequestBody @Valid
        baseUpdateRequest: BingoBoardBaseUpdateRequest
    ): ApiResponse<BingoBoardBaseUpdateResponse> {
        val updated = bingoBoardUpdateService.updateBase(bingoBoardId, memberId, baseUpdateRequest.command())
        return ApiResponse.OK(BingoBoardBaseUpdateResponse.fromBingoBoard(updated))
    }

    @PatchMapping("/{bingoBoardId}/publish-info")
    fun updateBingoMembersPeriod(
        @PathVariable @DecryptId(ObfuscationType.BINGO_BOARD) bingoBoardId: Long,
        @AuthenticationPrincipal
        memberId: Long,
        @RequestBody @Valid
        memberPeriodUpdateRequest: BingoBoardMembersPeriodUpdateRequest
    ): ApiResponse<BingoBoardMembersPeriodUpdateResponse> {
        val updated = bingoBoardUpdateService.updateMembersPeriod(bingoBoardId, memberId, memberPeriodUpdateRequest.command())
        return ApiResponse.OK(BingoBoardMembersPeriodUpdateResponse.fromBingoBoard(updated))
    }

    @PatchMapping("/{bingoBoardId}/memo")
    fun updateMemo(
        @PathVariable @DecryptId(ObfuscationType.BINGO_BOARD) bingoBoardId: Long,
        @AuthenticationPrincipal
        memberId: Long,
        @RequestBody @Valid
        memoUpdateRequest: BingoBoardMemoUpdateRequest
    ): ApiResponse<BingoBoardMemoUpdateResponse> {
        val updated = bingoBoardUpdateService.updateMemo(bingoBoardId, memberId, memoUpdateRequest.memo)
        return ApiResponse.OK(BingoBoardMemoUpdateResponse.fromBingoBoard(updated))
    }

    @PatchMapping("/{bingoBoardId}/open")
    fun updateOpen(
        @PathVariable @DecryptId(ObfuscationType.BINGO_BOARD) bingoBoardId: Long,
        @AuthenticationPrincipal
        memberId: Long,
        @RequestBody @Valid
        openUpdateRequest: BingoBoardOpenUpdateRequest
    ): ApiResponse<BingoBoardOpenUpdateResponse> {
        val updated = bingoBoardUpdateService.updateOpen(bingoBoardId, memberId, openUpdateRequest.command())
        return ApiResponse.OK(BingoBoardOpenUpdateResponse.fromBingoBoard(updated))
    }
}
