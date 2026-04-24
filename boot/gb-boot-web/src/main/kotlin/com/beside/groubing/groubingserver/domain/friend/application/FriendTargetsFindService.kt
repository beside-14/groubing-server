package com.beside.groubing.groubingserver.domain.friend.application

import com.beside.groubing.groubingserver.domain.friend.dao.FriendFindDao
import com.beside.groubing.groubingserver.domain.member.domain.port.MemberQueryRepository
import com.beside.groubing.groubingserver.domain.member.payload.response.MemberFindResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FriendTargetsFindService(
    private val memberQueryRepository: MemberQueryRepository,
    private val friendFindDao: FriendFindDao
) {
    fun findFriendTargets(myMemberId: Long): List<MemberFindResponse> {
        val members = memberQueryRepository.findAllSortedByNickname()
        val receivedCounterparts = friendFindDao.findAllReceivedBy(myMemberId)
            .filter { !it.status.isReject() }
            .map { it.memberId }
            .toSet()
        val sentCounterparts = friendFindDao.findAllSentBy(myMemberId)
            .filter { !it.status.isReject() }
            .map { it.memberId }
            .toSet()

        return members
            .filter { it.id !in receivedCounterparts }
            .filter { it.id !in sentCounterparts }
            .filter { it.id != myMemberId }
            .map(::MemberFindResponse)
    }
}
