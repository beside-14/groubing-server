package com.beside.groubing.groubingserver.domain.member.api

import com.beside.groubing.groubingserver.domain.member.application.MemberUnblockingService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/members")
class MemberUnblockingApi(
    private val memberUnblockingService: MemberUnblockingService
) {
    @DeleteMapping("/{requesterId}/blocked-list/{id}")
    fun unblocking(
        @PathVariable requesterId: Long,
        @PathVariable id: Long
    ) {
        memberUnblockingService.unblocking(id)
    }
}
