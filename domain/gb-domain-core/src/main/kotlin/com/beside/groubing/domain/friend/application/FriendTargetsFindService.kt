package com.beside.groubing.domain.friend.application

import com.beside.groubing.domain.friend.domain.FriendStatus
import com.beside.groubing.domain.friend.domain.port.FriendQueryRepository
import com.beside.groubing.domain.member.domain.Member
import com.beside.groubing.domain.member.domain.port.MemberQueryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FriendTargetsFindService(
    private val memberQueryRepository: MemberQueryRepository,
    private val friendQueryRepository: FriendQueryRepository
) {
    fun findFriendTargets(myMemberId: Long): List<Member> {
        return memberQueryRepository.findAllSortedByNicknameExcluding(collectExcludedMemberIds(myMemberId))
    }

    private fun collectExcludedMemberIds(myMemberId: Long): Set<Long> {
        val received = friendQueryRepository.findAllReceivedBy(myMemberId, NON_REJECTED).map { it.memberId }
        val sent = friendQueryRepository.findAllSentBy(myMemberId, NON_REJECTED).map { it.memberId }
        return (received + sent + myMemberId).toSet()
    }

    companion object {
        private val NON_REJECTED = setOf(FriendStatus.PENDING, FriendStatus.ACCEPT)
    }
}
