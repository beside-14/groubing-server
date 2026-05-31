package com.beside.groubing.domain.bingo.api

import com.beside.groubing.domain.bingo.application.BingoItemCompleteService
import com.beside.groubing.domain.bingo.payload.response.BingoCalculatingResponse
import com.beside.groubing.global.domain.id.ObfuscationType
import com.beside.groubing.global.id.DecryptId
import com.beside.groubing.global.response.ApiResponse
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/bingo-boards")
class BingoItemCompleteApi(
    private val bingoItemCompleteService: BingoItemCompleteService
) {
    @PatchMapping("/{id}/bingo-items/{bingoItemId}/complete")
    fun completeBingoItem(
        @PathVariable @DecryptId(ObfuscationType.BINGO_BOARD) id: Long,
        @PathVariable @DecryptId(ObfuscationType.BINGO_ITEM) bingoItemId: Long,
        @AuthenticationPrincipal memberId: Long
    ): ApiResponse<BingoCalculatingResponse> {
        val bingoMap = bingoItemCompleteService.complete(id, bingoItemId, memberId)
        return ApiResponse.OK(BingoCalculatingResponse.fromBingoMap(bingoMap))
    }

    @PatchMapping("/{id}/bingo-items/{bingoItemId}/cancel")
    fun cancelBingoItem(
        @PathVariable @DecryptId(ObfuscationType.BINGO_BOARD) id: Long,
        @PathVariable @DecryptId(ObfuscationType.BINGO_ITEM) bingoItemId: Long,
        @AuthenticationPrincipal memberId: Long
    ): ApiResponse<BingoCalculatingResponse> {
        val bingoMap = bingoItemCompleteService.cancel(id, bingoItemId, memberId)
        return ApiResponse.OK(BingoCalculatingResponse.fromBingoMap(bingoMap))
    }
}
