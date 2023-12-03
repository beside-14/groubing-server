package com.beside.groubing.groubingserver.domain.member.domain

import org.springframework.data.jpa.repository.JpaRepository

interface MemberRepository : JpaRepository<Member, Long> {

    fun findByEmail(email: String): Member?

    fun findByEmailAndMemberType(email: String, memberType: MemberType): Member?

    fun findByFcmTokenNotNullAndNotificationReceiveIsTrue(): List<Member>

    fun existsByEmail(email: String): Boolean

    fun existsByNickname(nickname: String): Boolean

    fun countByIdIn(ids: Collection<Long>): Int
}
