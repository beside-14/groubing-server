package com.beside.groubing

import com.beside.groubing.domain.member.domain.MemberRole
import com.beside.groubing.domain.member.domain.MemberType
import com.beside.groubing.domain.member.entity.MemberEntity
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.alphanumeric
import io.kotest.property.arbitrary.email
import io.kotest.property.arbitrary.single
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.stringPattern

fun aMember(memberId: Long): MemberEntity {
    return MemberEntity(
        id = memberId,
        email = "test${memberId}@gmail.com",
        password = "1234",
        nickname = "test${memberId}",
        role = MemberRole.MEMBER,
        memberType = MemberType.CLASSIC
    )
}

fun aMember(
    email: String = Arb.email(Arb.string(5, 10, Codepoint.alphanumeric()), Arb.stringPattern("groubing\\.com"))
        .single(),
    password: String = Arb.string(minSize = 8, maxSize = 20, codepoints = Codepoint.alphanumeric()).single(),
    nickname: String = Arb.string(minSize = 8, maxSize = 20, codepoints = Codepoint.alphanumeric()).single()
): MemberEntity {
    return MemberEntity(
        email = email,
        password = password,
        nickname = nickname,
        role = MemberRole.MEMBER,
        memberType = MemberType.CLASSIC
    )
}
