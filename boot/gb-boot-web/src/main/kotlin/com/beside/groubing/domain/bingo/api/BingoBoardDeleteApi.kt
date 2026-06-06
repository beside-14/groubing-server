package com.beside.groubing.domain.bingo.api

import com.beside.groubing.domain.bingo.application.BingoBoardDeleteService
import com.beside.groubing.domain.common.id.ObfuscationType
import com.beside.groubing.global.id.DecryptId
import com.beside.groubing.global.response.ApiResponse
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
    @DeleteMapping("/{bingoBoardId}")
    fun delete(
        @AuthenticationPrincipal
        memberId: Long,
        @PathVariable @DecryptId(ObfuscationType.BINGO_BOARD) bingoBoardId: Long
    ): ApiResponse<Unit> {
        bingoBoardDeleteService.delete(memberId = memberId, boardId = bingoBoardId)
        return ApiResponse.OK(Unit)
    }
}
