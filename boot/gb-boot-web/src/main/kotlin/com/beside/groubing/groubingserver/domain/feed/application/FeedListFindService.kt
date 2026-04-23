package com.beside.groubing.groubingserver.domain.feed.application

import com.beside.groubing.groubingserver.domain.feed.dao.FeedListFindDao
import com.beside.groubing.groubingserver.domain.feed.payload.response.FeedResponse
import com.beside.groubing.groubingserver.domain.friend.dao.FriendFindDao
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FeedListFindService(
    private val feedListFindDao: FeedListFindDao,

    private val friendFindDao: FriendFindDao,

) {
    fun findAllFeeds(myMemberId: Long): List<FeedResponse> {
        val friends = friendFindDao.findAllByInviterIdOrInviteeId(myMemberId)
        val findFeeds = feedListFindDao.findFeeds(friendIds = friends.values.map { it.id }
            .plus(myMemberId))
        val friendRequestReceivedList = friendFindDao.findAllByInviteeId(myMemberId)
            .filter { !it.status.isReject() }
        val friendRequestSendList = friendFindDao.findAllByInviterId(myMemberId)
            .filter { !it.status.isReject() }
        findFeeds.map { it.checkFriendRequest(friendRequestReceivedList, friendRequestSendList) }
        return findFeeds
    }
}
