package com.beside.groubing.domain.blockedmember.api

import com.beside.groubing.domain.blockedmember.application.BlockedMemberFindService
import com.beside.groubing.domain.blockedmember.payload.response.BlockedMemberResponse
import com.beside.groubing.global.response.ApiResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/blocked-members")
class BlockedMemberFindApi(
    private val blockedMemberFindService: BlockedMemberFindService
) {
    @GetMapping
    fun find(
        @AuthenticationPrincipal memberId: Long,
        @PageableDefault pageable: Pageable
    ): ApiResponse<List<BlockedMemberResponse>> {
        val response = blockedMemberFindService.find(memberId).map(BlockedMemberResponse::of)
        return ApiResponse.OK(response)
    }
}
