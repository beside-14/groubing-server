package com.beside.groubing.domain.member.api

import com.beside.groubing.domain.auth.domain.SocialType
import com.beside.groubing.global.response.ApiResponse
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/social-types")
class SocialTypeListFindApi(
) {
    @GetMapping
    fun getSocialTypes(): ApiResponse<List<SocialType>> {
        return ApiResponse.OK(SocialType.values().toList())
    }
}
