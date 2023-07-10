package com.beside.groubing.groubingserver.domain.member.api

import com.beside.groubing.groubingserver.domain.member.application.MemberFriendAcceptService
import com.beside.groubing.groubingserver.domain.member.application.MemberFriendDeleteService
import com.beside.groubing.groubingserver.domain.member.payload.request.MemberFriendChangeStatusRequest
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/members")
class MemberFriendChangeStatusApi(
    private val memberFriendAcceptService: MemberFriendAcceptService,
    private val memberFriendDeleteService: MemberFriendDeleteService
) {
    @PatchMapping("/{inviterId}/friends/{id}")
    fun changeStatus(
        @PathVariable inviterId: Long,
        @PathVariable id: Long,
        @RequestBody request: MemberFriendChangeStatusRequest
    ) {
        if (request.accept) {
            memberFriendAcceptService.accept(id)
        } else {
            memberFriendDeleteService.delete(id)
        }
    }
}
