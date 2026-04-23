package com.beside.groubing.groubingserver.domain.member.domain

import com.beside.groubing.groubingserver.domain.member.exception.MemberInputException

class Members(
    val members: List<Member>
) {
    private val byId: Map<Long, Member> = members.associateBy { it.id }

    fun find(id: Long): Member {
        return byId[id] ?: throw MemberInputException("존재하지 않는 유저 입니다.")
    }
}
