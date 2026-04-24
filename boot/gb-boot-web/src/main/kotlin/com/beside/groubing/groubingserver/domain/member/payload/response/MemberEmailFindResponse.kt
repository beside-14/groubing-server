package com.beside.groubing.groubingserver.domain.member.payload.response

import com.beside.groubing.groubingserver.domain.member.domain.Member

data class MemberEmailFindResponse(
    val id: Long,
    val email: String
) {
    companion object {
        fun of(member: Member): MemberEmailFindResponse = MemberEmailFindResponse(
            id = member.id,
            email = member.maskEmail()
        )
    }
}
