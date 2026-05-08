package com.beside.groubing.domain.bingo.api

import com.beside.groubing.domain.bingo.application.BingoItemUpdateService
import com.beside.groubing.domain.bingo.payload.request.BingoItemUpdateRequest
import com.beside.groubing.domain.bingo.payload.response.BingoItemResponse
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
    @PutMapping("/{id}/bingo-items/{bingoItemId}")
    fun updateBingoItem(
        @PathVariable id: Long,
        @PathVariable bingoItemId: Long,
        @AuthenticationPrincipal memberId: Long,
        @RequestBody @Valid
        bingoItemUpdateRequest: BingoItemUpdateRequest
    ): ApiResponse<BingoItemResponse> {
        val bingoItem = bingoItemUpdateService.update(id, bingoItemId, memberId, bingoItemUpdateRequest.command())
        return ApiResponse.OK(BingoItemResponse.fromBingoItem(bingoItem, memberId))
    }
}
