package com.beside.groubing.groubingserver.domain.member.application

import com.beside.groubing.groubingserver.domain.member.domain.BlockedMemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MemberUnblockingService(
    private val blockedMemberRepository: BlockedMemberRepository
) {
    fun unblocking(id: Long) {
        blockedMemberRepository.deleteById(id)
    }
}
