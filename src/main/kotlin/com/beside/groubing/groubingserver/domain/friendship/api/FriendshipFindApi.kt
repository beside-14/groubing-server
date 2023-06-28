package com.beside.groubing.groubingserver.domain.friendship.api

import com.beside.groubing.groubingserver.domain.member.application.MemberFindFriendsService
import com.beside.groubing.groubingserver.domain.member.payload.response.MemberDetailResponse
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
@RequestMapping("/api/friendships")
class FriendshipFindApi(
    private val memberFindFriendsService: MemberFindFriendsService
) {
    @GetMapping
    fun findFriends(
        @AuthenticationPrincipal memberId: Long,
        @RequestParam(required = false) nickname: String?,
        @PageableDefault pageable: Pageable
    ): ApiResponse<PageResponse<MemberDetailResponse>> {
        val response = when (nickname.isNullOrBlank()) {
            true -> memberFindFriendsService.findById(memberId, pageable)
            false -> memberFindFriendsService.findByIdAndNickname(memberId, pageable, nickname)
        }
        return ApiResponse.OK(PageResponse(response))
    }
}
