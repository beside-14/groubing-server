package com.beside.groubing.domain.member.api

import com.beside.groubing.domain.member.application.MemberProfileDeleteService
import com.beside.groubing.domain.common.id.ObfuscationType
import com.beside.groubing.global.id.DecryptId
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/members")
class MemberProfileDeleteApi(
    private val memberProfileDeleteService: MemberProfileDeleteService
) {
    @DeleteMapping("/{memberId}/profile")
    fun deleteProfile(
        @PathVariable @DecryptId(ObfuscationType.MEMBER) memberId: Long
    ) {
        memberProfileDeleteService.delete(memberId)
    }
}
