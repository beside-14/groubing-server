package com.beside.groubing.groubingserver.domain.friendship.api

import com.beside.groubing.groubingserver.domain.friendship.application.FriendshipAddService
import com.beside.groubing.groubingserver.domain.friendship.payload.request.FriendshipAddRequest
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/friendships")
class FriendshipAddApi(
    private val friendshipAddService: FriendshipAddService
) {
    @PostMapping
    fun add(
        @AuthenticationPrincipal inviterId: Long,
        @RequestBody
        @Validated
        request: FriendshipAddRequest
    ) {
        friendshipAddService.add(inviterId, request.inviteeId)
    }
}
