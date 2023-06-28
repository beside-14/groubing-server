package com.beside.groubing.groubingserver.domain.friendship.api

import com.beside.groubing.groubingserver.domain.friendship.application.FriendshipChangeStatusService
import com.beside.groubing.groubingserver.domain.friendship.domain.FriendshipStatus
import com.beside.groubing.groubingserver.domain.friendship.payload.request.FriendshipChangeStatusRequest
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/friendships")
class FriendshipChangeStatusApi(
    private val friendshipChangeStatusService: FriendshipChangeStatusService
) {
    @PatchMapping("/{id}")
    fun changeStatus(
        @PathVariable id: Long,
        @RequestBody request: FriendshipChangeStatusRequest
    ) {
        friendshipChangeStatusService.change(id, FriendshipStatus.acceptOrReject(request.accept))
    }
}
