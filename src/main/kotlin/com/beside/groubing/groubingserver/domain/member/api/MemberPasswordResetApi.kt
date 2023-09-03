package com.beside.groubing.groubingserver.domain.member.api

import com.beside.groubing.groubingserver.domain.member.application.MemberPasswordResetService
import com.beside.groubing.groubingserver.domain.member.payload.request.MemberPasswordResetRequest
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/members")
class MemberPasswordResetApi(
    private val memberPasswordResetService: MemberPasswordResetService
) {
    @PatchMapping("/{id}/password")
    fun resetPassword(
        @PathVariable id: Long,
        @RequestBody
        @Validated
        request: MemberPasswordResetRequest
    ) {
        memberPasswordResetService.reset(id, request.beforePassword, request.afterPassword)
    }
}
