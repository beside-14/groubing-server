package com.beside.groubing.groubingserver.domain.blocked.api

import com.beside.groubing.groubingserver.domain.blocked.application.BlockingMemberService
import com.beside.groubing.groubingserver.domain.blocked.payload.request.BlockingMemberRequest
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/blocked-members")
class BlockingMemberApi(
    private val blockingMemberService: BlockingMemberService
) {
    @PostMapping
    fun blocking(
        @AuthenticationPrincipal requesterId: Long,
        @RequestBody request: BlockingMemberRequest
    ) {
        blockingMemberService.blocking(requesterId, request.targetMemberId)
    }
}
