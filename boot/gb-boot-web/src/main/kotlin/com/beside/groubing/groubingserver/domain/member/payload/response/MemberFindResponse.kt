package com.beside.groubing.groubingserver.domain.member.payload.response

import com.beside.groubing.groubingserver.domain.member.domain.Member

class MemberFindResponse(
    val memberId: Long,
    val email: String?,
    val nickname: String,
    val profileUrl: String?,
) {
    constructor(member: Member) : this(
        memberId = member.id,
        email = member.email,
        nickname = member.nickname,
        profileUrl = member.profile?.url
    )
}
