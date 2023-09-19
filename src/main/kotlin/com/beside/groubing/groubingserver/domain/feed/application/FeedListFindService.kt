package com.beside.groubing.groubingserver.domain.feed.application

import com.beside.groubing.groubingserver.domain.feed.dao.FeedListFindDao
import com.beside.groubing.groubingserver.domain.feed.payload.response.FeedResponse
import com.beside.groubing.groubingserver.domain.friend.application.FriendFindService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FeedListFindService(
    private val feedListFindDao: FeedListFindDao,

    private val friendFindService: FriendFindService
) {
    fun findAllFeeds(memberId: Long): List<FeedResponse> {
        val friends = friendFindService.findAllByInviterIdOrInviteeId(memberId)
        return feedListFindDao.findFeeds(friendIds = friends.map { it.memberId }.plus(memberId))
    }
}
