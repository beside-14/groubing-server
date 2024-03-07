package com.beside.groubing.groubingserver.domain.friend.api

import com.beside.groubing.groubingserver.domain.friend.application.FriendTargetsFindService
import com.beside.groubing.groubingserver.domain.member.payload.response.MemberFindResponse
import com.beside.groubing.groubingserver.global.response.ApiResponse
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
        val memberFindResponses = friendTargetsFindService.findFriendTargets(loginMemberId)
        return ApiResponse.OK(memberFindResponses)
    }
}
