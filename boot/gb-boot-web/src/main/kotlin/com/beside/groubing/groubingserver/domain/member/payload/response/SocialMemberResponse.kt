package com.beside.groubing.groubingserver.domain.member.payload.response

import com.beside.groubing.groubingserver.domain.member.domain.Member

data class SocialMemberResponse(
    val id: Long,
    val email: String?,
    val nickname: String,
    val profileUrl: String?,
    val token: String,
    val hasNickname: Boolean
) {
    companion object {
        fun of(member: Member, token: String): SocialMemberResponse = SocialMemberResponse(
            id = member.id,
            email = member.email,
            nickname = member.nickname,
            profileUrl = member.profileUrl,
            token = token,
            hasNickname = member.nickname.isNotBlank()
        )
    }
}
