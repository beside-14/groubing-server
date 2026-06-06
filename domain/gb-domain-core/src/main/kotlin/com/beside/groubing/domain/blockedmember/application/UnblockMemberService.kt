package com.beside.groubing.domain.blockedmember.application

import com.beside.groubing.domain.blockedmember.domain.port.BlockedMemberRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UnblockMemberService(
    private val blockedMemberRepository: BlockedMemberRepository
) {
    fun unblock(requesterId: Long, targetMemberId: Long) {
        val blockedMember = blockedMemberRepository.findOne(requesterId, targetMemberId)
        blockedMemberRepository.delete(blockedMember)
    }
}
