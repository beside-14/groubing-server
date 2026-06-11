package com.beside.groubing.domain.member.payload.response

import com.beside.groubing.domain.auth.domain.AuthenticatedMember
import com.beside.groubing.domain.common.id.ObfuscationType
import com.beside.groubing.global.id.EncryptId

data class MemberResponse(
    @EncryptId(ObfuscationType.MEMBER)
    val id: Long,
    val nickname: String,
    val profileUrl: String?,
    val token: String,
    val notificationReceive: Boolean
) {
    companion object {
        fun of(authenticatedMember: AuthenticatedMember): MemberResponse {
            val member = authenticatedMember.member
            return MemberResponse(
                id = member.id,
                nickname = member.nickname,
                profileUrl = member.profileUrl,
                token = authenticatedMember.accessToken,
                notificationReceive = member.notificationReceive
            )
        }
    }
}
