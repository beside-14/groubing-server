package com.beside.groubing.groubingserver.domain.friendship.api

import com.beside.groubing.groubingserver.domain.friendship.application.FriendshipFindService
import com.beside.groubing.groubingserver.domain.friendship.payload.response.FriendResponse
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
    private val friendshipFindService: FriendshipFindService
) {
    @GetMapping
    fun findFriends(
        @AuthenticationPrincipal memberId: Long,
        @RequestParam(required = false) nickname: String?,
        @PageableDefault pageable: Pageable
    ): ApiResponse<PageResponse<FriendResponse>> {
        val response = when (nickname.isNullOrBlank()) {
            true -> friendshipFindService.findById(memberId, pageable)
            false -> friendshipFindService.findByIdAndNickname(memberId, pageable, nickname)
        }
        return ApiResponse.OK(PageResponse(response))
    }
}
