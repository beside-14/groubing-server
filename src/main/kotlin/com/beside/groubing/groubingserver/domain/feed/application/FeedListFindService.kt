package com.beside.groubing.groubingserver.domain.feed.application

import com.beside.groubing.groubingserver.domain.feed.dao.FeedListFindDao
import com.beside.groubing.groubingserver.domain.feed.payload.response.FeedResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class FeedListFindService(
    private val feedListFindDao: FeedListFindDao
) {
    fun findAllFeeds(): List<FeedResponse> {
        return feedListFindDao.findFeeds()
    }
}
