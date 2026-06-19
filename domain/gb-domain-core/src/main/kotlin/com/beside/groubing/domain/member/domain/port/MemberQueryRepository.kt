package com.beside.groubing.domain.member.domain.port

import com.beside.groubing.domain.member.domain.Member
import java.time.LocalDateTime

interface MemberQueryRepository {
    fun findById(id: Long): Member

    fun findExpiredMemberIds(threshold: LocalDateTime): List<Long>

    fun findActiveById(id: Long): Member

    fun findOneByLoginId(loginId: String): Member

    fun findAllSortedByNickname(): List<Member>

    fun findAllSortedByNicknameExcluding(excludedIds: Set<Long>): List<Member>

    fun findAll(ids: Collection<Long>): List<Member>

    fun findAllActive(ids: Collection<Long>): List<Member>

    fun existsByLoginId(loginId: String): Boolean

    fun existsByNickname(nickname: String): Boolean

    fun count(ids: Collection<Long>): Int
}
