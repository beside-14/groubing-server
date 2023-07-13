package com.beside.groubing.groubingserver.domain.friend.api

import com.beside.groubing.groubingserver.domain.friend.application.FriendAcceptService
import com.beside.groubing.groubingserver.domain.friend.application.FriendDeleteService
import com.beside.groubing.groubingserver.domain.friend.payload.request.FriendChangeStatusRequest
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/friends")
class FriendChangeStatusApi(
    private val friendAcceptService: FriendAcceptService,
    private val friendDeleteService: FriendDeleteService
) {
    @PatchMapping("/{id}")
    fun changeStatus(
        @AuthenticationPrincipal inviterId: Long,
        @PathVariable id: Long,
        @RequestBody request: FriendChangeStatusRequest
    ) {
        if (request.accept) {
            friendAcceptService.accept(id)
        } else {
            friendDeleteService.delete(id)
        }
    }
}
