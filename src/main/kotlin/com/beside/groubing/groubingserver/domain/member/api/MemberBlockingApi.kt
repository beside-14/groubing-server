package com.beside.groubing.groubingserver.domain.member.api

import com.beside.groubing.groubingserver.domain.member.application.MemberBlockingService
import com.beside.groubing.groubingserver.domain.member.payload.request.MemberBlockingRequest
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/members")
class MemberBlockingApi(
    private val memberBlockingService: MemberBlockingService
) {
    @PostMapping("/{requesterId}/blocked-list")
    fun blocking(
        @PathVariable requesterId: Long,
        @RequestBody request: MemberBlockingRequest
    ) {
        memberBlockingService.blocking(requesterId, request.targetMemberId)
    }
}
