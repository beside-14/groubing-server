package com.beside.groubing.groubingserver.domain.blockedmember.api

import com.beside.groubing.groubingserver.domain.blockedmember.application.BlockedMemberFindService
import com.beside.groubing.groubingserver.domain.blockedmember.payload.response.BlockedMemberResponse
import com.beside.groubing.groubingserver.global.response.ApiResponse
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
        val response = blockedMemberFindService.findById(memberId)
        return ApiResponse.OK(response)
    }
}
