package com.beside.groubing.groubingserver.domain.friend.application

import com.beside.groubing.groubingserver.domain.friend.dao.FriendFindDao
import com.beside.groubing.groubingserver.domain.member.payload.response.MemberFindResponse
import com.beside.groubing.groubingserver.domain.member.repository.MemberJpaRepository
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FriendTargetsFindService(
    private val memberJpaRepository: MemberJpaRepository,
    private val friendFindDao: FriendFindDao
) {
    fun findFriendTargets(myMemberId: Long): List<MemberFindResponse> {
        val members = memberJpaRepository.findAll(Sort.by(Sort.Direction.ASC, "nickname"))
        val friendRequestReceivedList = friendFindDao.findAllByInviteeId(myMemberId)
            .filter { !it.status.isReject() }
        val friendRequestSendList = friendFindDao.findAllByInviterId(myMemberId)
            .filter { !it.status.isReject() }

        return members
            .filter { member -> member.id !in friendRequestReceivedList.map { it.inviter.id } }
            .filter { member -> member.id !in friendRequestSendList.map { it.invitee.id } }
            .filter { member -> member.id != myMemberId }
            .map { MemberFindResponse(it.toDomain()) }
            .filter { it.memberId != myMemberId }
    }
}
