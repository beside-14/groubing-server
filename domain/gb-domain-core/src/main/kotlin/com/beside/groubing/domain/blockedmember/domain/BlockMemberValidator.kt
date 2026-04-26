package com.beside.groubing.domain.blockedmember.domain

import com.beside.groubing.domain.blockedmember.domain.port.BlockedMemberRepository
import com.beside.groubing.domain.blockedmember.exception.BlockedMemberInputException
import org.springframework.stereotype.Component

@Component
class BlockMemberValidator(
    private val blockedMemberRepository: BlockedMemberRepository
) {
    fun validate(requesterId: Long, targetMemberId: Long) {
        if (blockedMemberRepository.existsByRequesterIdAndTargetMemberId(requesterId, targetMemberId)) {
            throw BlockedMemberInputException("이미 차단한 유저입니다.")
        }
    }
}
