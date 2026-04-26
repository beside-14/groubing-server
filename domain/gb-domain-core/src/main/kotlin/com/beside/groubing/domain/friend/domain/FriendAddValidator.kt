package com.beside.groubing.domain.friend.domain

import com.beside.groubing.domain.blockedmember.domain.port.BlockedMemberRepository
import com.beside.groubing.domain.blockedmember.exception.BlockedMemberInputException
import com.beside.groubing.domain.member.domain.port.MemberQueryRepository
import org.springframework.stereotype.Component

@Component
class FriendAddValidator(
    private val blockedMemberRepository: BlockedMemberRepository,
    private val memberQueryRepository: MemberQueryRepository
) {
    fun validate(inviterId: Long, inviteeId: Long) {
        validateNotBlockedEitherWay(inviterId, inviteeId)
        validateMembersExist(inviterId, inviteeId)
    }

    private fun validateNotBlockedEitherWay(inviterId: Long, inviteeId: Long) {
        val isBlockedEitherWay = blockedMemberRepository.existsByRequesterIdAndTargetMemberId(inviterId, inviteeId) ||
            blockedMemberRepository.existsByRequesterIdAndTargetMemberId(inviteeId, inviterId)
        if (isBlockedEitherWay) {
            throw BlockedMemberInputException("내가 이미 차단했거나 상대방이 나를 차단했습니다.")
        }
    }

    private fun validateMembersExist(inviterId: Long, inviteeId: Long) {
        memberQueryRepository.findById(inviterId)
        memberQueryRepository.findById(inviteeId)
    }
}
