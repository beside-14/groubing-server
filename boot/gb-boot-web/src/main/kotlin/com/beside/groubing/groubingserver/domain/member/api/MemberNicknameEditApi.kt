package com.beside.groubing.groubingserver.domain.member.api

import com.beside.groubing.groubingserver.domain.member.application.MemberNicknameEditService
import com.beside.groubing.groubingserver.domain.member.payload.request.MemberNicknameEditRequest
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/members")
class MemberNicknameEditApi(
    private val memberNicknameEditService: MemberNicknameEditService
) {
    @PatchMapping("/{id}/nickname")
    fun editNickname(
        @PathVariable id: Long,
        @RequestBody
        @Validated
        request: MemberNicknameEditRequest
    ) {
        memberNicknameEditService.edit(id, request.nickname)
    }
}
