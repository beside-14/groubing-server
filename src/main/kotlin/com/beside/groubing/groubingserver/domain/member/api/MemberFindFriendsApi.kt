package com.beside.groubing.groubingserver.domain.member.api

import com.beside.groubing.groubingserver.domain.member.application.MemberFindFriendsService
import com.beside.groubing.groubingserver.domain.member.payload.response.MemberDetailResponse
import com.beside.groubing.groubingserver.global.response.ApiResponse
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
        @RequestParam(required = false) nickname: String?
    ): ApiResponse<List<MemberDetailResponse>> {
        val response = when (nickname.isNullOrBlank()) {
            true -> memberFindFriendsService.find(id)
            false -> memberFindFriendsService.findByNickname(id, nickname)
        }
        return ApiResponse.OK(response)
    }
}
