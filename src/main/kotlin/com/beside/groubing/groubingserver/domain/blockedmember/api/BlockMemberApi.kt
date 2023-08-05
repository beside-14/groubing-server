package com.beside.groubing.groubingserver.domain.blockedmember.api

import com.beside.groubing.groubingserver.domain.blockedmember.application.BlockMemberService
import com.beside.groubing.groubingserver.domain.blockedmember.payload.request.BlockingMemberRequest
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/blocked-members")
class BlockMemberApi(
    private val blockMemberService: BlockMemberService
) {
    @PostMapping
    fun block(
        @AuthenticationPrincipal memberId: Long,
        @RequestBody request: BlockingMemberRequest
    ) {
        blockMemberService.block(memberId, request.targetMemberId)
    }
}
