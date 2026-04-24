package com.beside.groubing.groubingserver.domain.feed.domain.port

import com.beside.groubing.groubingserver.domain.feed.domain.FeedItem

interface FeedListQueryRepository {
    fun findRecentCompleterMemberIds(memberIds: List<Long>, isFriend: Boolean): List<Long>

    fun findCompletedFeedItems(memberIds: List<Long>): List<FeedItem>
}
