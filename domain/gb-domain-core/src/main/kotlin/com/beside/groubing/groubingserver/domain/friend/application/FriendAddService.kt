package com.beside.groubing.groubingserver.domain.friend.application

import com.beside.groubing.groubingserver.domain.blockedmember.domain.port.BlockedMemberRepository
import com.beside.groubing.groubingserver.domain.blockedmember.exception.BlockedMemberInputException
import com.beside.groubing.groubingserver.domain.friend.domain.Friend
import com.beside.groubing.groubingserver.domain.friend.domain.FriendRelations
import com.beside.groubing.groubingserver.domain.friend.domain.port.FriendCommandRepository
import com.beside.groubing.groubingserver.domain.friend.domain.port.FriendQueryRepository
import com.beside.groubing.groubingserver.domain.member.domain.port.MemberQueryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FriendAddService(
    private val blockedMemberRepository: BlockedMemberRepository,
    private val friendQueryRepository: FriendQueryRepository,
    private val friendCommandRepository: FriendCommandRepository,
    private val memberQueryRepository: MemberQueryRepository
) {
    fun add(inviterId: Long, inviteeId: Long) {
        validateNotBlockedEitherWay(inviterId, inviteeId)
        validateMembersExist(inviterId, inviteeId)

        val friends = friendQueryRepository.findAllBetween(inviterId, inviteeId)
        if (friends.isEmpty()) {
            friendCommandRepository.save(Friend.create(inviterId, inviteeId))
            return
        }

        val rependable = FriendRelations(friends).findRependable(inviterId, inviteeId)
        friendCommandRepository.update(rependable.repend())
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
