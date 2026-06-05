package com.beside.groubing.domain.member.payload.response

import com.beside.groubing.domain.member.domain.Member
import com.beside.groubing.global.domain.id.ObfuscationType
import com.beside.groubing.global.id.EncryptId

data class MemberEmailFindResponse(
    @EncryptId(ObfuscationType.MEMBER)
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
