package com.beside.groubing.groubingserver.domain.blockedmember.domain.port

import com.beside.groubing.groubingserver.domain.blockedmember.domain.BlockedMember

interface BlockedMemberRepository {
    fun save(blockedMember: BlockedMember): BlockedMember

    fun findById(id: Long): BlockedMember?

    fun delete(blockedMember: BlockedMember)

    fun existsByRequesterIdAndTargetMemberId(requesterId: Long, targetMemberId: Long): Boolean
}
