package com.beside.groubing.domain.blockedmember.domain.port

import com.beside.groubing.domain.blockedmember.domain.BlockedMember
import com.beside.groubing.domain.blockedmember.domain.BlockedMemberTarget

interface BlockedMemberRepository {
    fun save(blockedMember: BlockedMember): BlockedMember

    fun findById(id: Long): BlockedMember

    fun delete(blockedMember: BlockedMember)

    fun exists(requesterId: Long, targetMemberId: Long): Boolean

    fun findAll(requesterId: Long): List<BlockedMemberTarget>
}
