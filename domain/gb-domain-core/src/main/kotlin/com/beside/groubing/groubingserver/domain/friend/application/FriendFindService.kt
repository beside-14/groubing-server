package com.beside.groubing.groubingserver.domain.friend.application

import com.beside.groubing.groubingserver.domain.friend.domain.FriendMember
import com.beside.groubing.groubingserver.domain.friend.domain.port.FriendQueryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FriendFindService(
    private val friendQueryRepository: FriendQueryRepository
) {
    fun findAllAcceptedOf(memberId: Long): List<FriendMember> =
        friendQueryRepository.findAllAcceptedOf(memberId)

    fun findAllReceivedPendingBy(inviteeId: Long): List<FriendMember> =
        friendQueryRepository.findAllReceivedBy(inviteeId).filter { it.status.isPending() }

    fun findAllSentPendingBy(inviterId: Long): List<FriendMember> =
        friendQueryRepository.findAllSentBy(inviterId).filter { it.status.isPending() }
}
