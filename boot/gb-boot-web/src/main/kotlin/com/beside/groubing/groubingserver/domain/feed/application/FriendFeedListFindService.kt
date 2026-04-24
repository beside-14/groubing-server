package com.beside.groubing.groubingserver.domain.feed.application

import com.beside.groubing.groubingserver.domain.feed.dao.FeedListFindDao
import com.beside.groubing.groubingserver.domain.feed.payload.response.FeedResponse
import com.beside.groubing.groubingserver.domain.friend.application.FriendFindService
import com.beside.groubing.groubingserver.domain.member.domain.port.MemberQueryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FriendFeedListFindService(
    private val feedListFindDao: FeedListFindDao,
    private val friendFindService: FriendFindService,
    private val memberQueryRepository: MemberQueryRepository
) {
    fun findFriendFeeds(memberId: Long): List<FeedResponse> {
        val friendIds = friendFindService.findAllAcceptedOf(memberId).map { it.memberId }
        if (friendIds.isEmpty()) return emptyList()

        val completerIds = feedListFindDao.findRecentCompleterMemberIds(friendIds, isFriend = true)
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
