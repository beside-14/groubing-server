package com.beside.groubing.domain.bingo.api

import com.beside.groubing.domain.bingo.application.BingoItemShuffleService
import com.beside.groubing.domain.bingo.domain.map.Direction
import com.beside.groubing.domain.bingo.payload.response.BingoLineResponse
import com.beside.groubing.global.domain.id.ObfuscationType
import com.beside.groubing.global.id.DecryptId
import com.beside.groubing.global.response.ApiResponse
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
    fun shuffle(
        @PathVariable @DecryptId(ObfuscationType.BINGO_BOARD) id: Long,
        @AuthenticationPrincipal memberId: Long,
    ): ApiResponse<List<BingoLineResponse>> {
        val bingoMap = bingoItemShuffleService.shuffle(memberId = memberId, boardId = id)
        val responses = bingoMap.getBingoLines(Direction.HORIZONTAL)
            .map { BingoLineResponse.fromBingoLine(it, memberId) }
        return ApiResponse.OK(responses)
    }
}
