package com.beside.groubing.groubingserver.domain.member.api

import com.beside.groubing.groubingserver.domain.member.application.MemberFriendAddService
import com.beside.groubing.groubingserver.domain.member.payload.request.MemberFriendAddRequest
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/members")
class MemberFriendAddApi(
    private val memberFriendAddService: MemberFriendAddService
) {
    @PostMapping("/{inviterId}/friends")
    fun add(
        @PathVariable inviterId: Long,
        @RequestBody
        @Validated
        request: MemberFriendAddRequest
    ) {
        memberFriendAddService.add(inviterId, request.inviteeId)
        // TODO("친구 요청 푸시 발송 기능 구현하기")
    }
}
