package com.beside.groubing.domain.friend.api

import com.beside.groubing.domain.friend.application.FriendTargetsFindService
import com.beside.groubing.domain.member.payload.response.MemberFindResponse
import com.beside.groubing.global.response.ApiResponse
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/friends")
class FriendTargetsFindApi(
    private val friendTargetsFindService: FriendTargetsFindService
) {
    @GetMapping("/targets")
    fun findAllMembers(@AuthenticationPrincipal loginMemberId: Long): ApiResponse<List<MemberFindResponse>> {
        val response = friendTargetsFindService.findFriendTargets(loginMemberId).map(::MemberFindResponse)
        return ApiResponse.OK(response)
    }
}
