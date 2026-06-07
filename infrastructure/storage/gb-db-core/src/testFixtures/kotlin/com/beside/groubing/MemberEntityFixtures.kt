package com.beside.groubing

import com.beside.groubing.domain.member.domain.MemberRole
import com.beside.groubing.domain.member.domain.MemberType
import com.beside.groubing.domain.member.entity.MemberEntity
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.alphanumeric
import io.kotest.property.arbitrary.single
import io.kotest.property.arbitrary.string

fun aMember(memberId: Long): MemberEntity {
    return MemberEntity(
        id = memberId,
        loginId = "test$memberId",
        password = "1234",
        nickname = "test$memberId",
        role = MemberRole.MEMBER,
        memberType = MemberType.CLASSIC
    )
}

fun aMember(
    loginId: String = Arb.string(minSize = 4, maxSize = 20, codepoints = Codepoint.alphanumeric()).single().lowercase(),
    password: String = Arb.string(minSize = 8, maxSize = 20, codepoints = Codepoint.alphanumeric()).single(),
    nickname: String = Arb.string(minSize = 8, maxSize = 20, codepoints = Codepoint.alphanumeric()).single()
): MemberEntity {
    return MemberEntity(
        loginId = loginId,
        password = password,
        nickname = nickname,
        role = MemberRole.MEMBER,
        memberType = MemberType.CLASSIC
    )
}
