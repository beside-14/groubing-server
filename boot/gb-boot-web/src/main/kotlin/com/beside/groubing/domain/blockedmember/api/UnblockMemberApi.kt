package com.beside.groubing.domain.blockedmember.api

import com.beside.groubing.domain.blockedmember.application.UnblockMemberService
import com.beside.groubing.global.domain.id.ObfuscationType
import com.beside.groubing.global.id.DecryptId
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
        @PathVariable @DecryptId(ObfuscationType.BLOCKED_MEMBER) id: Long
    ) {
        unblockMemberService.unblock(memberId, id)
    }
}
