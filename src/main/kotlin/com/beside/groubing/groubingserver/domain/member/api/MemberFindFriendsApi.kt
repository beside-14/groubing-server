package com.beside.groubing.groubingserver.domain.member.api

import com.beside.groubing.groubingserver.domain.member.application.MemberFindFriendsService
import com.beside.groubing.groubingserver.domain.member.payload.response.MemberDetailResponse
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
class MemberFindFriendsApi(
    private val memberFindFriendsService: MemberFindFriendsService
) {
    @GetMapping("/{id}/friends")
    fun findFriends(
        @PathVariable id: Long,
        @RequestParam(required = false) nickname: String?,
        @PageableDefault pageable: Pageable
    ): ApiResponse<PageResponse<MemberDetailResponse>> {
        val response = when (nickname.isNullOrBlank()) {
            true -> memberFindFriendsService.findById(id, pageable)
            false -> memberFindFriendsService.findByIdAndNickname(id, pageable, nickname)
        }
        return ApiResponse.OK(PageResponse(response))
    }
}
