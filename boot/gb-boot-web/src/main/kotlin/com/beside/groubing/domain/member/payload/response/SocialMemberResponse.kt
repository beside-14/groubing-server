package com.beside.groubing.domain.member.payload.response

import com.beside.groubing.domain.auth.domain.AuthenticatedMember
import com.beside.groubing.domain.common.id.ObfuscationType
import com.beside.groubing.global.id.EncryptId

data class SocialMemberResponse(
    @EncryptId(ObfuscationType.MEMBER)
    val id: Long,
    val email: String?,
    val nickname: String,
    val profileUrl: String?,
    val token: String,
    val hasNickname: Boolean
) {
    companion object {
        fun of(authenticatedMember: AuthenticatedMember): SocialMemberResponse {
            val member = authenticatedMember.member
            return SocialMemberResponse(
                id = member.id,
                email = member.email,
                nickname = member.nickname,
                profileUrl = member.profileUrl,
                token = authenticatedMember.accessToken,
                hasNickname = member.hasNickname()
            )
        }
    }
}
