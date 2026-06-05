package com.beside.groubing.domain.friend.api

import com.beside.groubing.domain.friend.application.FriendAcceptService
import com.beside.groubing.global.domain.id.ObfuscationType
import com.beside.groubing.global.id.DecryptId
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
        @PathVariable @DecryptId(ObfuscationType.FRIEND) id: Long
    ) {
        friendAcceptService.accept(memberId = memberId, id = id)
    }
}
