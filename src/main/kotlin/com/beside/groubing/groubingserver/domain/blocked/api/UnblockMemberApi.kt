package com.beside.groubing.groubingserver.domain.blocked.api

import com.beside.groubing.groubingserver.domain.blocked.application.UnblockMemberService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/blocked-members")
class UnblockMemberApi(
    private val unblockMemberService: UnblockMemberService
) {
    @DeleteMapping("/{id}")
    fun unblock(
        @AuthenticationPrincipal memberId: Long,
        @PathVariable id: Long
    ) {
        unblockMemberService.unblock(id)
    }
}
