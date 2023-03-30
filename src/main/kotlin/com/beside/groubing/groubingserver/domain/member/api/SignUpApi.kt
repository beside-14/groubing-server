package com.beside.groubing.groubingserver.domain.member.api

import com.beside.groubing.groubingserver.domain.member.application.SignUpService
import com.beside.groubing.groubingserver.domain.member.payload.request.SignUpRequest
import com.beside.groubing.groubingserver.domain.member.payload.response.SignUpResponse
import com.beside.groubing.groubingserver.global.response.ApiResponse
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
    fun signUp(@RequestBody request: SignUpRequest
    ): ApiResponse<SignUpResponse> {
        val signUpResponse = signUpService.signUp(request.command())
        return ApiResponse.OK(signUpResponse)
    }
}
