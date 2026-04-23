package com.beside.groubing.groubingserver.domain.bingo.api

import com.beside.groubing.groubingserver.domain.bingo.application.BingoMemberLeaveService
import com.beside.groubing.groubingserver.global.response.ApiResponse
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/bingo-boards")
class BingoMemberLeaveApi(
    private val bingoMemberLeaveService: BingoMemberLeaveService
) {

    @PostMapping("/{id}/leave")
    fun leave(
        @AuthenticationPrincipal
        memberId: Long,
        @PathVariable id: Long
    ): ApiResponse<Unit> {
        bingoMemberLeaveService.leaveBingoBoard(memberId, id)
        return ApiResponse.OK(Unit)
    }
}
