package com.beside.groubing.groubingserver.domain.member.api

import com.beside.groubing.groubingserver.domain.member.application.MemberEmailFindService
import com.beside.groubing.groubingserver.domain.member.payload.request.MemberEmailFindRequest
import com.beside.groubing.groubingserver.domain.member.payload.response.MemberEmailFindResponse
import com.beside.groubing.groubingserver.global.response.ApiResponse
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/members")
class MemberEmailFindApi(
    private val memberEmailFindService: MemberEmailFindService
) {
    @PostMapping("/find-email")
    fun findEmail(
        @RequestBody
        @Validated
        request: MemberEmailFindRequest
    ): ApiResponse<MemberEmailFindResponse> {
        val response = memberEmailFindService.find(request.email)
        return ApiResponse.OK(response)
    }
}
