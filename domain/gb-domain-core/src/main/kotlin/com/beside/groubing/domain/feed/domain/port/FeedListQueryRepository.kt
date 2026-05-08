package com.beside.groubing.domain.feed.domain.port

import com.beside.groubing.domain.feed.domain.FeedItem

interface FeedListQueryRepository {
    fun findRecentCompleterMemberIds(memberIds: List<Long>, isFriend: Boolean): List<Long>

    fun findCompletedFeedItems(memberIds: List<Long>): List<FeedItem>
}
