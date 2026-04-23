package com.beside.groubing.groubingserver.domain.blockedmember.repository

import com.beside.groubing.groubingserver.domain.blockedmember.domain.BlockedMember
import com.beside.groubing.groubingserver.domain.blockedmember.domain.port.BlockedMemberRepository
import com.beside.groubing.groubingserver.domain.blockedmember.entity.BlockedMemberEntity
import org.springframework.stereotype.Repository

@Repository
class BlockedMemberRepositoryAdapter(
    private val blockedMemberJpaRepository: BlockedMemberJpaRepository
) : BlockedMemberRepository {
    override fun save(blockedMember: BlockedMember): BlockedMember {
        return blockedMemberJpaRepository.save(BlockedMemberEntity.from(blockedMember)).toDomain()
    }

    override fun findById(id: Long): BlockedMember? {
        return blockedMemberJpaRepository.findById(id).map { it.toDomain() }.orElse(null)
    }

    override fun delete(blockedMember: BlockedMember) {
        blockedMemberJpaRepository.deleteById(blockedMember.id)
    }

    override fun existsByRequesterIdAndTargetMemberId(requesterId: Long, targetMemberId: Long): Boolean {
        return blockedMemberJpaRepository.existsByRequesterIdAndTargetMemberId(requesterId, targetMemberId)
    }
}
