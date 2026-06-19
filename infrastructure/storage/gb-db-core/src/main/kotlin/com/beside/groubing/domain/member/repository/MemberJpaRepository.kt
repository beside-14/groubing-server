package com.beside.groubing.domain.member.repository

import com.beside.groubing.domain.member.entity.MemberEntity
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface MemberJpaRepository : JpaRepository<MemberEntity, Long> {
    fun findByIdAndActiveTrue(id: Long): MemberEntity?

    @Query("select m.id from MemberEntity m where m.deletedAt <= :threshold and m.cleanedAt is null")
    fun findExpiredMemberIds(@Param("threshold") threshold: LocalDateTime): List<Long>

    fun findByLoginIdAndActiveTrue(loginId: String): MemberEntity?

    fun existsByLoginIdAndActiveTrue(loginId: String): Boolean

    fun existsByNicknameAndActiveTrue(nickname: String): Boolean

    fun countByIdInAndActiveTrue(ids: Collection<Long>): Int

    fun findAllByActiveTrue(sort: Sort): List<MemberEntity>

    fun findAllByIdInAndActiveTrue(ids: Collection<Long>): List<MemberEntity>

    fun findAllByIdNotInAndActiveTrue(excludedIds: Set<Long>, sort: Sort): List<MemberEntity>
}
