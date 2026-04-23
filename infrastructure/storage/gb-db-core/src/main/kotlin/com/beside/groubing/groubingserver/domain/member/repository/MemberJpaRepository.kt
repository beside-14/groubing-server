package com.beside.groubing.groubingserver.domain.member.repository

import com.beside.groubing.groubingserver.domain.member.domain.MemberType
import com.beside.groubing.groubingserver.domain.member.entity.MemberEntity
import org.springframework.data.jpa.repository.JpaRepository

interface MemberJpaRepository : JpaRepository<MemberEntity, Long> {
    fun findByEmail(email: String): MemberEntity?

    fun findByEmailAndMemberType(email: String, memberType: MemberType): MemberEntity?

    fun findByFcmTokenNotNullAndNotificationReceiveIsTrue(): List<MemberEntity>

    fun existsByEmail(email: String): Boolean

    fun existsByNickname(nickname: String): Boolean

    fun countByIdIn(ids: Collection<Long>): Int
}
