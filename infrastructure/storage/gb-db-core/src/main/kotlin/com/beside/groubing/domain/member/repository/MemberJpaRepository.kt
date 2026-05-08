package com.beside.groubing.domain.member.repository

import com.beside.groubing.domain.member.domain.MemberType
import com.beside.groubing.domain.member.entity.MemberEntity
import org.springframework.data.jpa.repository.JpaRepository

interface MemberJpaRepository : JpaRepository<MemberEntity, Long> {
    fun findByEmail(email: String): MemberEntity?

    fun findByEmailAndMemberType(email: String, memberType: MemberType): MemberEntity?

    fun existsByEmail(email: String): Boolean

    fun existsByNickname(nickname: String): Boolean

    fun countByIdIn(ids: Collection<Long>): Int

    fun findAllByIdNotIn(excludedIds: Set<Long>, sort: org.springframework.data.domain.Sort): List<MemberEntity>
}
