package com.beside.groubing.domain.friend.api

import com.beside.groubing.domain.friend.application.FriendRejectService
import com.beside.groubing.domain.common.id.ObfuscationType
import com.beside.groubing.global.id.DecryptId
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/friends")
class FriendRejectApi(
    private val friendRejectService: FriendRejectService
) {
    @PatchMapping("/{friendId}/reject")
    fun reject(
        @AuthenticationPrincipal memberId: Long,
        @PathVariable @DecryptId(ObfuscationType.FRIEND) friendId: Long
    ) {
        friendRejectService.reject(memberId = memberId, id = friendId)
    }
}
