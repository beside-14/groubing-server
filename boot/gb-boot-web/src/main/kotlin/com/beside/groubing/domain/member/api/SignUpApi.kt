package com.beside.groubing.domain.member.api

import com.beside.groubing.domain.auth.application.SignUpService
import com.beside.groubing.domain.member.payload.request.SignUpRequest
import com.beside.groubing.domain.member.payload.response.MemberResponse
import com.beside.groubing.global.response.ApiResponse
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/members")
class SignUpApi(
    private val signUpService: SignUpService
) {
    @PostMapping
    fun signUp(
        @RequestBody
        @Validated
        request: SignUpRequest
    ): ApiResponse<MemberResponse> {
        val authenticatedMember = signUpService.signUp(request.command())
        return ApiResponse.OK(MemberResponse.of(authenticatedMember))
    }
}
