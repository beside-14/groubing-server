package com.beside.groubing.domain.bingo.api

import com.beside.groubing.domain.bingo.application.BingoBoardFindService
import com.beside.groubing.domain.bingo.payload.response.BingoBoardDetailResponse
import com.beside.groubing.global.domain.id.ObfuscationType
import com.beside.groubing.global.id.DecryptId
import com.beside.groubing.global.response.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/bingo-boards")
class BingoBoardFindApi(
    private val bingoBoardFindService: BingoBoardFindService
) {
    @GetMapping("/{bingoBoardId}")
    fun getBingoBoard(
        @RequestParam @DecryptId(ObfuscationType.MEMBER) memberId: Long,
        @PathVariable @DecryptId(ObfuscationType.BINGO_BOARD) bingoBoardId: Long
    ): ApiResponse<BingoBoardDetailResponse> {
        val detail = bingoBoardFindService.findOne(memberId, bingoBoardId)
        return ApiResponse.OK(BingoBoardDetailResponse.of(detail))
    }
}
