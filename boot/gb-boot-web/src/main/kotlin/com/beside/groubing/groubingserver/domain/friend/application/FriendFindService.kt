package com.beside.groubing.groubingserver.domain.friend.application

import com.beside.groubing.groubingserver.domain.friend.dao.FriendFindDao
import com.beside.groubing.groubingserver.domain.friend.payload.response.FriendRequestResponse
import com.beside.groubing.groubingserver.domain.friend.payload.response.FriendResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FriendFindService(
    private val friendFindDao: FriendFindDao
) {
    fun findAllAcceptedOf(memberId: Long): List<FriendResponse> {
        return friendFindDao.findAllAcceptedOf(memberId).map(FriendResponse::of)
    }

    fun findAllReceivedPendingBy(inviteeId: Long): List<FriendRequestResponse> {
        return friendFindDao.findAllReceivedBy(inviteeId)
            .filter { it.status.isPending() }
            .map(FriendRequestResponse::of)
    }

    fun findAllSentPendingBy(inviterId: Long): List<FriendRequestResponse> {
        return friendFindDao.findAllSentBy(inviterId)
            .filter { it.status.isPending() }
            .map(FriendRequestResponse::of)
    }
}
