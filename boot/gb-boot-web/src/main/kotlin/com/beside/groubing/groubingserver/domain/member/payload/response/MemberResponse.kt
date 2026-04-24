package com.beside.groubing.groubingserver.domain.member.payload.response

import com.beside.groubing.groubingserver.domain.auth.domain.AuthenticatedMember

data class MemberResponse(
    val id: Long,
    val email: String,
    val nickname: String,
    val profileUrl: String?,
    val token: String,
    val notificationReceive: Boolean
) {
    companion object {
        fun of(authenticatedMember: AuthenticatedMember): MemberResponse {
            val member = authenticatedMember.member
            val email = checkNotNull(member.email) { "email 이 존재하지 않는 계정입니다." }
            return MemberResponse(
                id = member.id,
                email = email,
                nickname = member.nickname,
                profileUrl = member.profileUrl,
                token = authenticatedMember.accessToken,
                notificationReceive = member.notificationReceive
            )
        }
    }
}
