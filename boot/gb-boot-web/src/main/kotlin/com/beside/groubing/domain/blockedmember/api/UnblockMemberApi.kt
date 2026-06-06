package com.beside.groubing.domain.blockedmember.api

import com.beside.groubing.domain.blockedmember.application.UnblockMemberService
import com.beside.groubing.domain.common.id.ObfuscationType
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
    @DeleteMapping("/{targetMemberId}")
    fun unblock(
        @AuthenticationPrincipal memberId: Long,
        @PathVariable @DecryptId(ObfuscationType.MEMBER) targetMemberId: Long
    ) {
        unblockMemberService.unblock(memberId, targetMemberId)
    }
}
