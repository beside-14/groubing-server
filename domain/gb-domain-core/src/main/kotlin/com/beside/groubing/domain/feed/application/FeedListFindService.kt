package com.beside.groubing.domain.feed.application

import com.beside.groubing.domain.feed.domain.FeedEntry
import com.beside.groubing.domain.feed.domain.FeedEntry.Companion.MAX_FEED_ITEMS
import com.beside.groubing.domain.feed.domain.port.FeedListQueryRepository
import com.beside.groubing.domain.friend.domain.FriendStatus
import com.beside.groubing.domain.friend.domain.port.FriendQueryRepository
import com.beside.groubing.domain.member.domain.port.MemberQueryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FeedListFindService(
    private val feedListQueryRepository: FeedListQueryRepository,
    private val friendQueryRepository: FriendQueryRepository,
    private val memberQueryRepository: MemberQueryRepository
) {
    fun findAllFeeds(myMemberId: Long): List<FeedEntry> {
        val excludeIds = friendQueryRepository.findAllAcceptedOf(myMemberId).map { it.memberId } + myMemberId
        val entries = composeEntries(memberIds = excludeIds, isFriend = false)
        if (entries.isEmpty()) return emptyList()

        val receivedMemberIds = friendQueryRepository.findAllReceivedBy(myMemberId, NON_REJECTED)
            .map { it.memberId }
            .toSet()
        val sentMemberIds = friendQueryRepository.findAllSentBy(myMemberId, NON_REJECTED)
            .map { it.memberId }
            .toSet()
        return entries.map {
            it.copy(
                isFriendRequestReceived = it.memberId in receivedMemberIds,
                isFriendRequestSent = it.memberId in sentMemberIds
            )
        }
    }

    private fun composeEntries(memberIds: List<Long>, isFriend: Boolean): List<FeedEntry> {
        val completerIds = feedListQueryRepository.findRecentCompleterMemberIds(memberIds, isFriend)
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

    private fun FeedEntry.copy(
        isFriendRequestReceived: Boolean,
        isFriendRequestSent: Boolean
    ): FeedEntry = FeedEntry(
        memberId = memberId,
        nickname = nickname,
        profileUrl = profileUrl,
        itemTitles = itemTitles,
        isFriendRequestReceived = isFriendRequestReceived,
        isFriendRequestSent = isFriendRequestSent
    )

    companion object {
        private val NON_REJECTED = setOf(FriendStatus.PENDING, FriendStatus.ACCEPT)
    }
}
