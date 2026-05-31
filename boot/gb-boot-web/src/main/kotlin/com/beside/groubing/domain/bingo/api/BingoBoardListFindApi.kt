package com.beside.groubing.domain.bingo.api

import com.beside.groubing.domain.bingo.application.BingoBoardListFindService
import com.beside.groubing.domain.bingo.payload.response.BingoBoardOverviewResponse
import com.beside.groubing.global.domain.id.ObfuscationType
import com.beside.groubing.global.id.DecryptId
import com.beside.groubing.global.response.ApiResponse
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/bingo-boards")
class BingoBoardListFindApi(
    private val bingoBoardListFindService: BingoBoardListFindService
) {
    @GetMapping
    fun findAll(
        @RequestParam @DecryptId(ObfuscationType.MEMBER) memberId: Long,
        @AuthenticationPrincipal loginMemberId: Long
    ): ApiResponse<List<BingoBoardOverviewResponse>> {
        val bingoBoards = bingoBoardListFindService.find(memberId, loginMemberId)
        val responses = bingoBoards.map { BingoBoardOverviewResponse.fromBingoBoard(it, memberId) }
        return ApiResponse.OK(responses)
    }
}
