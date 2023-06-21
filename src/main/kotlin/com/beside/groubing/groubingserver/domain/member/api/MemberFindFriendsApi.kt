package com.beside.groubing.groubingserver.domain.member.api

import com.beside.groubing.groubingserver.domain.member.application.MemberFindFriendsService
import com.beside.groubing.groubingserver.domain.member.payload.response.MemberDetailResponse
import com.beside.groubing.groubingserver.global.response.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/members")
class MemberFindFriendsApi(
    private val memberFindFriendsService: MemberFindFriendsService
) {
    @GetMapping("/{id}/friends")
    fun findFriends(@PathVariable id: Long): ApiResponse<List<MemberDetailResponse>> {
        val response = memberFindFriendsService.find(id)
        return ApiResponse.OK(response)
    }

    @GetMapping("/{id}/friends/{nickname}")
    fun findFriends(@PathVariable id: Long, @PathVariable nickname: String): ApiResponse<List<MemberDetailResponse>> {
        val response = memberFindFriendsService.findByNickname(id, nickname)
        return ApiResponse.OK(response)
    }
}
