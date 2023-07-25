package com.beside.groubing.groubingserver.domain.bingo.api

import com.beside.groubing.groubingserver.domain.bingo.application.BingoItemCompleteService
import com.beside.groubing.groubingserver.domain.bingo.payload.response.BingoCalculatingResponse
import com.beside.groubing.groubingserver.global.response.ApiResponse
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
        @PathVariable id: Long,
        @PathVariable bingoItemId: Long,
        @AuthenticationPrincipal memberId: Long
    ): ApiResponse<BingoCalculatingResponse> {
        val bingoCalculatingResponse = bingoItemCompleteService.completeBingoItem(id, bingoItemId, memberId)
        return ApiResponse.OK(bingoCalculatingResponse)
    }

    @PatchMapping("/{id}/bingo-items/{bingoItemId}/cancel")
    fun cancelBingoItem(
        @PathVariable id: Long,
        @PathVariable bingoItemId: Long,
        @AuthenticationPrincipal memberId: Long
    ): ApiResponse<BingoCalculatingResponse> {
        val bingoCalculatingResponse = bingoItemCompleteService.cancelBingoItem(id, bingoItemId, memberId)
        return ApiResponse.OK(bingoCalculatingResponse)
    }
}
