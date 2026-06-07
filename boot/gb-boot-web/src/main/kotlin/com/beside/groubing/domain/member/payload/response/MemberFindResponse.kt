package com.beside.groubing.domain.member.payload.response

import com.beside.groubing.domain.member.domain.Member
import com.beside.groubing.domain.common.id.ObfuscationType
import com.beside.groubing.global.id.EncryptId

class MemberFindResponse(
    @EncryptId(ObfuscationType.MEMBER)
    val memberId: Long,
    val nickname: String,
    val profileUrl: String?,
) {
    constructor(member: Member) : this(
        memberId = member.id,
        nickname = member.nickname,
        profileUrl = member.profileUrl
    )
}
