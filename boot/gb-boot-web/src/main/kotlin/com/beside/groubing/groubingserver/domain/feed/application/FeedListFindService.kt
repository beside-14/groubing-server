package com.beside.groubing.groubingserver.domain.feed.application

import com.beside.groubing.groubingserver.domain.feed.dao.FeedListFindDao
import com.beside.groubing.groubingserver.domain.feed.payload.response.FeedResponse
import com.beside.groubing.groubingserver.domain.friend.dao.FriendFindDao
import com.beside.groubing.groubingserver.domain.member.domain.port.MemberQueryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FeedListFindService(
    private val feedListFindDao: FeedListFindDao,
    private val friendFindDao: FriendFindDao,
    private val memberQueryRepository: MemberQueryRepository
) {
    fun findAllFeeds(myMemberId: Long): List<FeedResponse> {
        val excludeIds = friendFindDao.findAllAcceptedOf(myMemberId).map { it.memberId } + myMemberId
        val feeds = composeFeeds(memberIds = excludeIds, isFriend = false)

        val received = friendFindDao.findAllReceivedBy(myMemberId).filter { !it.status.isReject() }
        val sent = friendFindDao.findAllSentBy(myMemberId).filter { !it.status.isReject() }
        feeds.forEach { it.checkFriendRequest(received, sent) }
        return feeds
    }

    private fun composeFeeds(memberIds: List<Long>, isFriend: Boolean): List<FeedResponse> {
        val completerIds = feedListFindDao.findRecentCompleterMemberIds(memberIds, isFriend)
        if (completerIds.isEmpty()) return emptyList()

        val titlesByMember = feedListFindDao.findCompletedFeedItems(completerIds)
            .groupBy({ it.memberId }, { it.title })
        val members = memberQueryRepository.findAllByIdIn(completerIds).sortedBy { it.id }

        return members.mapNotNull { member ->
            val titles = titlesByMember[member.id].orEmpty().shuffled().take(MAX_FEED_ITEMS)
            if (titles.isEmpty()) null else FeedResponse.of(member, titles)
        }
    }

    companion object {
        private const val MAX_FEED_ITEMS = 5
    }
}
