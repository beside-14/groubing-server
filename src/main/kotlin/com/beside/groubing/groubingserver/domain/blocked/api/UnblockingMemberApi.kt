package com.beside.groubing.groubingserver.domain.blocked.api

import com.beside.groubing.groubingserver.domain.blocked.application.UnblockingMemberService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/blocked-members")
class UnblockingMemberApi(
    private val unblockingMemberService: UnblockingMemberService
) {
    @DeleteMapping("/{id}")
    fun unblocking(
        @AuthenticationPrincipal requesterId: Long,
        @PathVariable id: Long
    ) {
        unblockingMemberService.unblocking(id)
    }
}
