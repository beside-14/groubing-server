package com.beside.groubing.groubingserver.domain.friend.application

import com.beside.groubing.groubingserver.domain.friend.dao.FriendFindDao
import com.beside.groubing.groubingserver.domain.friend.payload.response.FriendRequestResponse
import com.beside.groubing.groubingserver.domain.friend.payload.response.FriendResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class FriendFindService(
    private val friendFindDao: FriendFindDao
) {
    fun findAllByInviterIdOrInviteeId(memberId: Long): List<FriendResponse> {
        val friends = friendFindDao.findAllByInviterIdOrInviteeId(memberId)
        return friends.map(::FriendResponse)
    }

    fun findAllByInviteeIdAndLastThreeMonths(inviteeId: Long): List<FriendRequestResponse> {
        val friendRequestList = friendFindDao.findAllByInviteeId(inviteeId)
        return friendRequestList.map(::FriendRequestResponse)
    }
}
