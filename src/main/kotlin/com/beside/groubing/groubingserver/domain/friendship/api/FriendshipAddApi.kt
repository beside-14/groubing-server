package com.beside.groubing.groubingserver.domain.friendship.api

import com.beside.groubing.groubingserver.domain.friendship.application.FriendshipAddService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/friendships")
class FriendshipAddApi(
    private val friendshipAddService: FriendshipAddService
) {
    @PostMapping
    fun add(
        @AuthenticationPrincipal inviterId: Long,
        @RequestParam inviteeId: Long
    ) {
        friendshipAddService.add(inviterId, inviteeId)
    }
}
