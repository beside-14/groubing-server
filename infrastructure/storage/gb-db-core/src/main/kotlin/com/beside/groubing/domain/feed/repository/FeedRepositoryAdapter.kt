package com.beside.groubing.domain.feed.repository

import com.beside.groubing.domain.feed.dao.FeedListFindDao
import com.beside.groubing.domain.feed.domain.FeedItem
import com.beside.groubing.domain.feed.domain.port.FeedListQueryRepository
import org.springframework.stereotype.Repository

@Repository
class FeedRepositoryAdapter(
    private val feedListFindDao: FeedListFindDao
) : FeedListQueryRepository {
    override fun findRecentCompleterMemberIds(memberIds: List<Long>, isFriend: Boolean): List<Long> =
        feedListFindDao.findRecentCompleterMemberIds(memberIds, isFriend)

    override fun findCompletedFeedItems(memberIds: List<Long>): List<FeedItem> =
        feedListFindDao.findCompletedFeedItems(memberIds).map { FeedItem(it.memberId, it.title) }
}
