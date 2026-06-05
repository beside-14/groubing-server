package com.beside.groubing.domain.bingo.api

import com.beside.groubing.domain.bingo.application.BingoItemUpdateService
import com.beside.groubing.domain.bingo.payload.request.BingoItemUpdateRequest
import com.beside.groubing.domain.bingo.payload.response.BingoItemResponse
import com.beside.groubing.global.domain.id.ObfuscationType
import com.beside.groubing.global.id.DecryptId
import com.beside.groubing.global.response.ApiResponse
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/bingo-boards")
class BingoItemUpdateApi(
    private val bingoItemUpdateService: BingoItemUpdateService
) {
    @PutMapping("/{bingoBoardId}/bingo-items/{bingoItemId}")
    fun updateBingoItem(
        @PathVariable @DecryptId(ObfuscationType.BINGO_BOARD) bingoBoardId: Long,
        @PathVariable @DecryptId(ObfuscationType.BINGO_ITEM) bingoItemId: Long,
        @AuthenticationPrincipal memberId: Long,
        @RequestBody @Valid
        bingoItemUpdateRequest: BingoItemUpdateRequest
    ): ApiResponse<BingoItemResponse> {
        val bingoItem = bingoItemUpdateService.update(bingoBoardId, bingoItemId, memberId, bingoItemUpdateRequest.command())
        return ApiResponse.OK(BingoItemResponse.fromBingoItem(bingoItem, memberId))
    }
}
