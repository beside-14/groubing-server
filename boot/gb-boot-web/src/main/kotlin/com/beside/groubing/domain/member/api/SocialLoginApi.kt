package com.beside.groubing.domain.member.api

import com.beside.groubing.domain.auth.application.SocialLoginService
import com.beside.groubing.domain.member.payload.request.SocialLoginRequest
import com.beside.groubing.domain.member.payload.response.SocialMemberResponse
import com.beside.groubing.global.response.ApiResponse
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
        val authenticatedMember = socialLoginService.login(request.command())
        return ApiResponse.OK(SocialMemberResponse.of(authenticatedMember))
    }
}
