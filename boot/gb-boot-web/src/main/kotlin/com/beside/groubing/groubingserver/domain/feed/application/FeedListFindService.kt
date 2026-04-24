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
        val acceptedFriends = friendFindDao.findAllAcceptedOf(myMemberId)
        val findFeeds = feedListFindDao.findFeeds(
            friendIds = acceptedFriends.map { it.memberId }.plus(myMemberId)
        )
        val friendRequestReceivedList = friendFindDao.findAllReceivedBy(myMemberId)
            .filter { !it.status.isReject() }
        val friendRequestSendList = friendFindDao.findAllSentBy(myMemberId)
            .filter { !it.status.isReject() }
        findFeeds.map { it.checkFriendRequest(friendRequestReceivedList, friendRequestSendList) }
        return findFeeds
    }
}
