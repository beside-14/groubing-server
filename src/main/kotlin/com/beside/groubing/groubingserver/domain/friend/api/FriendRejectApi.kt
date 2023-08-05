package com.beside.groubing.groubingserver.domain.friend.api

import com.beside.groubing.groubingserver.domain.friend.application.FriendRejectService
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
    @PatchMapping("/{id}/reject")
    fun reject(
        @AuthenticationPrincipal memberId: Long,
        @PathVariable id: Long
    ) {
        friendRejectService.reject(memberId = memberId, id = id)
    }
}
