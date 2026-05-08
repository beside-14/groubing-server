package com.beside.groubing.domain.member.api

import com.beside.groubing.domain.auth.application.LoginService
import com.beside.groubing.domain.member.payload.request.LoginRequest
import com.beside.groubing.domain.member.payload.response.MemberResponse
import com.beside.groubing.global.response.ApiResponse
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/members")
class LoginApi(
    private val loginService: LoginService
) {
    @PostMapping("/login")
    fun login(
        @RequestBody
        @Validated
        request: LoginRequest
    ): ApiResponse<MemberResponse> {
        val authenticatedMember = loginService.login(request.command())
        return ApiResponse.OK(MemberResponse.of(authenticatedMember))
    }
}
