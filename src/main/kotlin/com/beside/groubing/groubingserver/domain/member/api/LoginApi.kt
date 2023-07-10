package com.beside.groubing.groubingserver.domain.member.api

import com.beside.groubing.groubingserver.domain.member.application.LoginService
import com.beside.groubing.groubingserver.domain.member.payload.request.LoginRequest
import com.beside.groubing.groubingserver.domain.member.payload.response.MemberLoginResponse
import com.beside.groubing.groubingserver.global.response.ApiResponse
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
    ): ApiResponse<MemberLoginResponse> {
        val loginResponse = loginService.login(request.command())
        return ApiResponse.OK(loginResponse)
    }
}
