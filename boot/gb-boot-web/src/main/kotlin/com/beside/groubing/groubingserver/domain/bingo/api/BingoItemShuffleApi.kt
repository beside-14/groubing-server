package com.beside.groubing.groubingserver.domain.bingo.api

import com.beside.groubing.groubingserver.domain.bingo.application.BingoItemShuffleService
import com.beside.groubing.groubingserver.domain.bingo.payload.response.BingoLineResponse
import com.beside.groubing.groubingserver.global.response.ApiResponse
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/bingo-boards")
class BingoItemShuffleApi(
    private val bingoItemShuffleService: BingoItemShuffleService
) {
    @PutMapping("/{id}/bingo-items")
    fun updateBingoItem(
        @PathVariable id: Long,
        @AuthenticationPrincipal memberId: Long,
    ): ApiResponse<List<BingoLineResponse>> {
        val bingoLineResponses = bingoItemShuffleService.shuffleBingoItems(memberId = memberId, boardId = id)
        return ApiResponse.OK(bingoLineResponses)
    }
}
