package com.beside.groubing.domain.blockedmember.application

import com.beside.groubing.domain.blockedmember.domain.port.BlockedMemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UnblockMemberService(
    private val blockedMemberRepository: BlockedMemberRepository
) {
    fun unblock(requesterId: Long, id: Long) {
        val blockedMember = blockedMemberRepository.findById(id)
        blockedMember.validateUnblockAuthority(requesterId)
        blockedMemberRepository.delete(blockedMember)
    }
}
