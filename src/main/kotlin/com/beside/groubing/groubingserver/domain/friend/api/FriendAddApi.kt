package com.beside.groubing.groubingserver.domain.friend.api

import com.beside.groubing.groubingserver.domain.friend.application.FriendAddService
import com.beside.groubing.groubingserver.domain.friend.payload.request.FriendAddRequest
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/friends")
class FriendAddApi(
    private val friendAddService: FriendAddService
) {
    @PostMapping
    fun add(
        @AuthenticationPrincipal memberId: Long,
        @RequestBody
        @Validated
        request: FriendAddRequest
    ) {
        friendAddService.add(memberId, request.inviteeId)
        // TODO("친구 요청 푸시 발송 기능 구현하기")
    }
}
