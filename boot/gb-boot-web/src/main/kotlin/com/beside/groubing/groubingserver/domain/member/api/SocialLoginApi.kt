package com.beside.groubing.groubingserver.domain.member.api

import com.beside.groubing.groubingserver.domain.member.application.SocialLoginService
import com.beside.groubing.groubingserver.domain.member.payload.request.SocialLoginRequest
import com.beside.groubing.groubingserver.domain.member.payload.response.SocialMemberResponse
import com.beside.groubing.groubingserver.global.response.ApiResponse
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/members")
class SocialLoginApi(
    private val socialLoginService: SocialLoginService
) {
    @PostMapping("/social-login")
    fun login(
        @RequestBody
        @Validated
        request: SocialLoginRequest
    ): ApiResponse<SocialMemberResponse> {
        val loginResponse = socialLoginService.login(request.command())
        return ApiResponse.OK(loginResponse)
    }
}
