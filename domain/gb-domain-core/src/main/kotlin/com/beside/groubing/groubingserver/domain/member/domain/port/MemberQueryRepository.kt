package com.beside.groubing.groubingserver.domain.member.domain.port

import com.beside.groubing.groubingserver.domain.member.domain.Member
import com.beside.groubing.groubingserver.domain.member.domain.MemberType

interface MemberQueryRepository {
    fun findById(id: Long): Member

    fun findByEmail(email: String): Member

    fun findByEmailAndMemberType(email: String, memberType: MemberType): Member

    fun findAllSortedByNickname(): List<Member>

    fun findAllByIdIn(ids: Collection<Long>): List<Member>

    fun existsByEmail(email: String): Boolean

    fun existsByNickname(nickname: String): Boolean

    fun countByIdIn(ids: Collection<Long>): Int
}
