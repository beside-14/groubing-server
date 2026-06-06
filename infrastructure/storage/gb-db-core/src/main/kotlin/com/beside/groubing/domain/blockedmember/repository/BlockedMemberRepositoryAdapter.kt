package com.beside.groubing.domain.blockedmember.repository

import com.beside.groubing.domain.blockedmember.dao.BlockedMemberFindDao
import com.beside.groubing.domain.blockedmember.domain.BlockedMember
import com.beside.groubing.domain.blockedmember.domain.BlockedMemberTarget
import com.beside.groubing.domain.blockedmember.domain.port.BlockedMemberRepository
import com.beside.groubing.domain.blockedmember.entity.BlockedMemberEntity
import com.beside.groubing.domain.blockedmember.exception.BlockedMemberInputException
import org.springframework.stereotype.Repository

@Repository
class BlockedMemberRepositoryAdapter(
    private val blockedMemberJpaRepository: BlockedMemberJpaRepository,
    private val blockedMemberFindDao: BlockedMemberFindDao
) : BlockedMemberRepository {
    override fun save(blockedMember: BlockedMember): BlockedMember {
        return blockedMemberJpaRepository.save(BlockedMemberEntity.from(blockedMember)).toDomain()
    }

    override fun findOne(requesterId: Long, targetMemberId: Long): BlockedMember {
        return blockedMemberJpaRepository.findByRequesterIdAndTargetMemberId(requesterId, targetMemberId)?.toDomain()
            ?: throw BlockedMemberInputException("차단 내역이 존재하지 않습니다.")
    }

    override fun delete(blockedMember: BlockedMember) {
        blockedMemberJpaRepository.deleteById(blockedMember.id)
    }

    override fun exists(requesterId: Long, targetMemberId: Long): Boolean {
        return blockedMemberJpaRepository.existsByRequesterIdAndTargetMemberId(requesterId, targetMemberId)
    }

    override fun findAll(requesterId: Long): List<BlockedMemberTarget> {
        return blockedMemberFindDao.findAllRequestedBy(requesterId).map {
            BlockedMemberTarget(
                id = it.id,
                email = it.email,
                nickname = it.nickname,
                profileFileName = it.profileFileName
            )
        }
    }
}
