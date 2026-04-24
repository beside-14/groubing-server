package com.beside.groubing.groubingserver.domain.friend.application

import com.beside.groubing.groubingserver.domain.friend.domain.port.FriendQueryRepository
import com.beside.groubing.groubingserver.domain.member.domain.Member
import com.beside.groubing.groubingserver.domain.member.domain.port.MemberQueryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FriendTargetsFindService(
    private val memberQueryRepository: MemberQueryRepository,
    private val friendQueryRepository: FriendQueryRepository
) {
    fun findFriendTargets(myMemberId: Long): List<Member> {
        val members = memberQueryRepository.findAllSortedByNickname()
        val receivedCounterparts = friendQueryRepository.findAllReceivedBy(myMemberId)
            .filter { !it.status.isReject() }
            .map { it.memberId }
            .toSet()
        val sentCounterparts = friendQueryRepository.findAllSentBy(myMemberId)
            .filter { !it.status.isReject() }
            .map { it.memberId }
            .toSet()

        return members
            .filter { it.id !in receivedCounterparts }
            .filter { it.id !in sentCounterparts }
            .filter { it.id != myMemberId }
    }
}
