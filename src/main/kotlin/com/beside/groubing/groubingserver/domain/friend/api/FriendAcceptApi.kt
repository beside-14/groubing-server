package com.beside.groubing.groubingserver.domain.friend.api

import com.beside.groubing.groubingserver.domain.friend.application.FriendAcceptService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/friends")
class FriendAcceptApi(
    private val friendAcceptService: FriendAcceptService
) {
    @PatchMapping("/{id}/accept")
    fun accept(
        @AuthenticationPrincipal memberId: Long,
        @PathVariable id: Long
    ) {
        friendAcceptService.accept(id)
    }
}
