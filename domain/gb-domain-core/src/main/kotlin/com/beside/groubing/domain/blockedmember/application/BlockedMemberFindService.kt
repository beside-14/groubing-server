package com.beside.groubing.domain.blockedmember.application

import com.beside.groubing.domain.blockedmember.domain.BlockedMemberTarget
import com.beside.groubing.domain.blockedmember.domain.port.BlockedMemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class BlockedMemberFindService(
    private val blockedMemberRepository: BlockedMemberRepository
) {
    fun find(requesterId: Long): List<BlockedMemberTarget> {
        return blockedMemberRepository.findAll(requesterId)
    }
}
