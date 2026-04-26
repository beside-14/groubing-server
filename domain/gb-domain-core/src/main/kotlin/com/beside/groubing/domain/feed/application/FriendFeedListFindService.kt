package com.beside.groubing.domain.feed.application

import com.beside.groubing.domain.feed.domain.FeedEntry
import com.beside.groubing.domain.feed.domain.port.FeedListQueryRepository
import com.beside.groubing.domain.friend.domain.port.FriendQueryRepository
import com.beside.groubing.domain.member.domain.port.MemberQueryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FriendFeedListFindService(
    private val feedListQueryRepository: FeedListQueryRepository,
    private val friendQueryRepository: FriendQueryRepository,
    private val memberQueryRepository: MemberQueryRepository
) {
    fun findFriendFeeds(memberId: Long): List<FeedEntry> {
        val friendIds = friendQueryRepository.findAllAcceptedOf(memberId).map { it.memberId }
        if (friendIds.isEmpty()) return emptyList()

        val completerIds = feedListQueryRepository.findRecentCompleterMemberIds(friendIds, isFriend = true)
        if (completerIds.isEmpty()) return emptyList()

        val titlesByMember = feedListQueryRepository.findCompletedFeedItems(completerIds)
            .groupBy({ it.memberId }, { it.title })
        val members = memberQueryRepository.findAll(completerIds).sortedBy { it.id }

        return members.mapNotNull { member ->
            val titles = titlesByMember[member.id].orEmpty().shuffled().take(MAX_FEED_ITEMS)
            if (titles.isEmpty()) {
                null
            } else {
                FeedEntry(
                    memberId = member.id,
                    nickname = member.nickname,
                    profileUrl = member.profileUrl,
                    itemTitles = titles,
                    isFriendRequestReceived = false,
                    isFriendRequestSent = false
                )
            }
        }
    }

    companion object {
        private const val MAX_FEED_ITEMS = 5
    }
}
