package com.beside.groubing.domain.member.repository

import com.beside.groubing.domain.member.domain.MemberType
import com.beside.groubing.domain.member.entity.MemberEntity
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository

interface MemberJpaRepository : JpaRepository<MemberEntity, Long> {
    fun findByIdAndActiveTrue(id: Long): MemberEntity?

    fun findByEmailAndActiveTrue(email: String): MemberEntity?

    fun findByEmailAndMemberTypeAndActiveTrue(email: String, memberType: MemberType): MemberEntity?

    fun existsByEmailAndActiveTrue(email: String): Boolean

    fun existsByNicknameAndActiveTrue(nickname: String): Boolean

    fun countByIdInAndActiveTrue(ids: Collection<Long>): Int

    fun findAllByActiveTrue(sort: Sort): List<MemberEntity>

    fun findAllByIdInAndActiveTrue(ids: Collection<Long>): List<MemberEntity>

    fun findAllByIdNotInAndActiveTrue(excludedIds: Set<Long>, sort: Sort): List<MemberEntity>
}
