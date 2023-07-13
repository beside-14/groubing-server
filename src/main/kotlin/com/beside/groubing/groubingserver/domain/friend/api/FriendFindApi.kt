package com.beside.groubing.groubingserver.domain.friend.api

import com.beside.groubing.groubingserver.domain.friend.application.FriendFindService
import com.beside.groubing.groubingserver.domain.friend.payload.response.FriendResponse
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
@RequestMapping("/api/friends")
class FriendFindApi(
    private val friendFindService: FriendFindService
) {
    @GetMapping
    fun findFriends(
        @AuthenticationPrincipal inviterId: Long,
        @RequestParam(required = false) nickname: String?,
        @PageableDefault pageable: Pageable
    ): ApiResponse<PageResponse<FriendResponse>> {
        val response = when (nickname.isNullOrBlank()) {
            true -> friendFindService.findById(inviterId, pageable)
            false -> friendFindService.findByIdAndNickname(inviterId, pageable, nickname)
        }
        return ApiResponse.OK(PageResponse(response))
    }
}
