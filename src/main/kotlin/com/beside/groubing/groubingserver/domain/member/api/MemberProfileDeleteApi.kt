package com.beside.groubing.groubingserver.domain.member.api

import com.beside.groubing.groubingserver.domain.member.application.MemberProfileDeleteService
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/members")
class MemberProfileDeleteApi(
    private val memberProfileDeleteService: MemberProfileDeleteService
) {
    @DeleteMapping("/{id}/profile")
    fun deleteProfile(
        @PathVariable id: Long
    ) {
        memberProfileDeleteService.delete(id)
    }
}
