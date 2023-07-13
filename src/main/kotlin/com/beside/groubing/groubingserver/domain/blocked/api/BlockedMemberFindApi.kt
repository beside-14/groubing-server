package com.beside.groubing.groubingserver.domain.blocked.api

import com.beside.groubing.groubingserver.domain.blocked.application.BlockedMemberFindService
import com.beside.groubing.groubingserver.domain.blocked.payload.response.BlockedMemberResponse
import com.beside.groubing.groubingserver.global.response.ApiResponse
import com.beside.groubing.groubingserver.global.response.PageResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/blocked-members")
class BlockedMemberFindApi(
    private val blockedMemberFindService: BlockedMemberFindService
) {
    @GetMapping
    fun find(
        @AuthenticationPrincipal requesterId: Long,
        @RequestParam(required = false) nickname: String?,
        @PageableDefault pageable: Pageable
    ): ApiResponse<PageResponse<BlockedMemberResponse>> {
        val response = when (nickname.isNullOrBlank()) {
            true -> blockedMemberFindService.findById(requesterId, pageable)
            false -> blockedMemberFindService.findByIdAndNickname(requesterId, pageable, nickname)
        }
        return ApiResponse.OK(PageResponse(response))
    }
}
