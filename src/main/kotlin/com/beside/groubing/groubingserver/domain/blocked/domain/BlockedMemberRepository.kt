package com.beside.groubing.groubingserver.domain.blocked.domain

import org.springframework.data.jpa.repository.JpaRepository

interface BlockedMemberRepository : JpaRepository<BlockedMember, Long> {
    fun existsByRequesterIdAndTargetMemberId(requesterId: Long, targetMemberId: Long): Boolean
}
