package com.beside.groubing.groubingserver.domain.blocked.application

import com.beside.groubing.groubingserver.domain.blocked.domain.BlockedMemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UnblockingMemberService(
    private val blockedMemberRepository: BlockedMemberRepository
) {
    fun unblocking(id: Long) {
        blockedMemberRepository.deleteById(id)
    }
}
