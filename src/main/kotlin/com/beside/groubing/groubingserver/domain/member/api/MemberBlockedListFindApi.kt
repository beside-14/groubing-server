package com.beside.groubing.groubingserver.domain.member.api

import com.beside.groubing.groubingserver.domain.member.application.MemberBlockedListFindService
import com.beside.groubing.groubingserver.domain.member.payload.response.MemberSummaryResponse
import com.beside.groubing.groubingserver.global.response.ApiResponse
import com.beside.groubing.groubingserver.global.response.PageResponse
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/members")
class MemberBlockedListFindApi(
    private val memberBlockedListFindService: MemberBlockedListFindService
) {
    @GetMapping("/{requesterId}/blocked-list")
    fun find(
        @PathVariable requesterId: Long,
        @RequestParam(required = false) nickname: String?,
        @PageableDefault pageable: Pageable
    ): ApiResponse<PageResponse<MemberSummaryResponse>> {
        val response = when (nickname.isNullOrBlank()) {
            true -> memberBlockedListFindService.findById(requesterId, pageable)
            false -> memberBlockedListFindService.findByIdAndNickname(requesterId, pageable, nickname)
        }
        return ApiResponse.OK(PageResponse(response))
    }
}
