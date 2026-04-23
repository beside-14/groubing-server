package com.beside.groubing.groubingserver.domain.blockedmember.repository

import com.beside.groubing.groubingserver.domain.blockedmember.entity.BlockedMemberEntity
import org.springframework.data.jpa.repository.JpaRepository

interface BlockedMemberJpaRepository : JpaRepository<BlockedMemberEntity, Long> {
    fun existsByRequesterIdAndTargetMemberId(requesterId: Long, targetMemberId: Long): Boolean
}
