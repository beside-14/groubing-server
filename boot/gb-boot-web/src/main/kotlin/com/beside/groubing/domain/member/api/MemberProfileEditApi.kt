package com.beside.groubing.domain.member.api

import com.beside.groubing.domain.member.application.MemberProfileEditService
import com.beside.groubing.domain.member.payload.response.MemberProfileResponse
import com.beside.groubing.global.domain.file.application.FileProvider
import com.beside.groubing.global.response.ApiResponse
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
        val newProfile = FileProvider.upload(profile)
        val profileUrl = memberProfileEditService.edit(id, newProfile)
        return ApiResponse.OK(MemberProfileResponse(profileUrl))
    }
}
