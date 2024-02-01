package com.beside.groubing.groubingserver.domain.member.api

import com.beside.groubing.groubingserver.domain.member.application.MemberAllFindService
import com.beside.groubing.groubingserver.domain.member.payload.response.MemberFindResponse
import com.beside.groubing.groubingserver.global.response.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/members")
class MemberAllFindApi(
    private val memberAllFindService: MemberAllFindService
) {
    @GetMapping
    fun findAllMembers(): ApiResponse<List<MemberFindResponse>> {
        val memberFindResponses = memberAllFindService.findAllMembers()
        return ApiResponse.OK(memberFindResponses)
    }
}
