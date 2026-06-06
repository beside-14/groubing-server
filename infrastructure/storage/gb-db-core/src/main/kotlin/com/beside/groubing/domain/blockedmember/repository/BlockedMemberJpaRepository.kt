package com.beside.groubing.domain.blockedmember.repository

import com.beside.groubing.domain.blockedmember.entity.BlockedMemberEntity
import org.springframework.data.jpa.repository.JpaRepository

interface BlockedMemberJpaRepository : JpaRepository<BlockedMemberEntity, Long> {
    fun existsByRequesterIdAndTargetMemberId(requesterId: Long, targetMemberId: Long): Boolean

    fun findByRequesterIdAndTargetMemberId(requesterId: Long, targetMemberId: Long): BlockedMemberEntity?
}
