package com.beside.groubing.groubingserver.domain.friend.api

import com.beside.groubing.groubingserver.domain.friend.application.FriendFindService
import com.beside.groubing.groubingserver.domain.friend.payload.response.FriendRequestResponse
import com.beside.groubing.groubingserver.domain.friend.payload.response.FriendResponse
import com.beside.groubing.groubingserver.global.response.ApiResponse
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/friends")
class FriendFindApi(
    private val friendFindService: FriendFindService
) {
    @GetMapping
    fun findFriends(
        @AuthenticationPrincipal memberId: Long
    ): ApiResponse<List<FriendResponse>> {
        val response = friendFindService.findAllByInviterIdOrInviteeId(memberId)
        return ApiResponse.OK(response)
    }

    @GetMapping("/requests")
    fun findFriendRequests(
        @AuthenticationPrincipal memberId: Long
    ): ApiResponse<List<FriendRequestResponse>> {
        val response = friendFindService.findAllByInviteeIdAndLastThreeMonths(memberId)
        return ApiResponse.OK(response)
    }
}
