package com.beside.groubing.groubingserver.domain.friend.application

import com.beside.groubing.groubingserver.domain.friend.dao.FriendFindDao
import com.beside.groubing.groubingserver.domain.member.domain.MemberRepository
import com.beside.groubing.groubingserver.domain.member.payload.response.MemberFindResponse
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FriendTargetsFindService(
    private val memberRepository: MemberRepository,

    private val friendFindDao: FriendFindDao
) {
    fun findFriendTargets(myMemberId: Long): List<MemberFindResponse> {
        val members = memberRepository.findAll(Sort.by(Sort.Direction.ASC, "nickname"))
        val friendRequestReceivedList = friendFindDao.findAllByInviteeId(myMemberId)
            .filter { !it.status.isReject() }
        val friendRequestSendList = friendFindDao.findAllByInviterId(myMemberId)
            .filter { !it.status.isReject() }

        return members
            .filter { member -> member.id !in friendRequestReceivedList.map { it.inviter.id } }
            .filter { member -> member.id !in friendRequestSendList.map { it.invitee.id } }
            .map { MemberFindResponse(it) }
    }

}
