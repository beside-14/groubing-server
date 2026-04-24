package com.beside.groubing.groubingserver.domain.blockedmember.application

import com.beside.groubing.groubingserver.domain.blockedmember.domain.port.BlockedMemberRepository
import com.beside.groubing.groubingserver.domain.blockedmember.exception.BlockedMemberInputException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class UnblockMemberService(
    private val blockedMemberRepository: BlockedMemberRepository
) {
    fun unblock(requesterId: Long, id: Long) {
        val blockedMember = blockedMemberRepository.findById(id)
        if (!blockedMember.isBlockedMember(requesterId)) {
            throw BlockedMemberInputException("차단을 해제할 권한이 없습니다.")
        }
        blockedMemberRepository.delete(blockedMember)
    }
}
