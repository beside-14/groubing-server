package com.beside.groubing.groubingserver.domain.bingo.api

import com.beside.groubing.groubingserver.domagin.bingo.dto.BingoBoardResponse
import com.beside.groubing.groubingserver.domain.bingo.application.BingoBoardFindService
import com.beside.groubing.groubingserver.global.response.ApiResponse
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/bingoboards")
class BingoBoardFindApi(
    private val bingoBoardFindService: BingoBoardFindService
) {
    @GetMapping("/{id}")
    fun getBingoBoard(@AuthenticationPrincipal memberId: Long, @PathVariable id: Long): ApiResponse<BingoBoardResponse> {
        val bingoBoardResponse = bingoBoardFindService.findBingoBoard(memberId, id)
        return ApiResponse.OK(bingoBoardResponse)
    }
}
