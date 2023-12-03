package com.beside.groubing.groubingserver.domain.member.domain

import com.beside.groubing.groubingserver.domain.member.exception.MemberInputException

class MemberMap(
    val members: List<Member>
) {
    private val values: Map<Long, Member?> = members.associateBy { member -> member.id }

    fun find(id: Long): Member {
        return values[id] ?: throw MemberInputException("존재하지 않는 유저 입니다.")
    }
}
