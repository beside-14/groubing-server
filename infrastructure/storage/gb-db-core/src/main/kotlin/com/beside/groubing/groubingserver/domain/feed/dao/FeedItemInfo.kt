package com.beside.groubing.groubingserver.domain.feed.dao

import com.querydsl.core.annotations.QueryProjection

class FeedItemInfo @QueryProjection constructor(
    val memberId: Long,

    val title: String
)
