package com.beside.groubing.groubingserver.domain.member.api

import com.beside.groubing.groubingserver.domain.member.application.MemberProfileEditService
import com.beside.groubing.groubingserver.domain.member.payload.response.MemberProfileResponse
import com.beside.groubing.groubingserver.global.response.ApiResponse
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/members")
class MemberProfileEditApi(
    private val memberProfileEditService: MemberProfileEditService
) {
    @PatchMapping("/{id}/profile")
    fun editProfile(
        @PathVariable id: Long,
        @RequestPart profile: MultipartFile
    ): ApiResponse<MemberProfileResponse> {
        val profileUrl = memberProfileEditService.edit(id, profile)
        val response = MemberProfileResponse(profileUrl)
        return ApiResponse.OK(response)
    }
}
