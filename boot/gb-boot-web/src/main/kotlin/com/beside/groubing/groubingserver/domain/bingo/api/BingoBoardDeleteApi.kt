package com.beside.groubing.groubingserver.domain.bingo.api

import com.beside.groubing.groubingserver.domain.bingo.application.BingoBoardDeleteService
import com.beside.groubing.groubingserver.global.response.ApiResponse
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/bingo-boards")
class BingoBoardDeleteApi(
    private val bingoBoardDeleteService: BingoBoardDeleteService
) {
    @DeleteMapping("/{id}")
    fun delete(
        @AuthenticationPrincipal
        memberId: Long,
        @PathVariable id: Long
    ): ApiResponse<Unit> {
        bingoBoardDeleteService.deleteBingoBoard(memberId = memberId, boardId = id)
        return ApiResponse.OK(Unit)
    }
}
