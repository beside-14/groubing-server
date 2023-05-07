package com.beside.groubing.groubingserver.domain.bingo.api

import com.beside.groubing.groubingserver.domain.bingo.application.BingoItemUpdateService
import com.beside.groubing.groubingserver.domain.bingo.payload.request.BingoItemUpdateRequest
import com.beside.groubing.groubingserver.domain.bingo.payload.response.BingoItemResponse
import com.beside.groubing.groubingserver.global.response.ApiResponse
import jakarta.validation.Valid
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/bingoboards")
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
        val updateBingoItemResponse = bingoItemUpdateService.updateBingoItem(id, bingoItemId, memberId, bingoItemUpdateRequest.command())
        return ApiResponse.OK(updateBingoItemResponse)
    }
}
