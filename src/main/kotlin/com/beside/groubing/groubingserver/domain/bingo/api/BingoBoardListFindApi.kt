package com.beside.groubing.groubingserver.domain.bingo.api

import com.beside.groubing.groubingserver.domain.bingo.application.BingoBoardListFindService
import com.beside.groubing.groubingserver.domain.bingo.payload.response.BingoBoardOverviewResponse
import com.beside.groubing.groubingserver.global.response.ApiResponse
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
    fun getBingoBoard(
        @RequestParam memberId: Long,
        @AuthenticationPrincipal loginMemberId: Long
    ): ApiResponse<List<BingoBoardOverviewResponse>> {
        val bingoBoardOverviewResponses = bingoBoardListFindService.findBingoBoardList(memberId, loginMemberId)
        return ApiResponse.OK(bingoBoardOverviewResponses)
    }
}
