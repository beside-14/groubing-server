package com.beside.groubing.domain.bingo.api

import com.beside.groubing.domain.bingo.application.BingoMemberLeaveService
import com.beside.groubing.global.domain.id.ObfuscationType
import com.beside.groubing.global.id.DecryptId
import com.beside.groubing.global.response.ApiResponse
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
        @PathVariable @DecryptId(ObfuscationType.BINGO_BOARD) id: Long
    ): ApiResponse<Unit> {
        bingoMemberLeaveService.leave(memberId, id)
        return ApiResponse.OK(Unit)
    }
}
