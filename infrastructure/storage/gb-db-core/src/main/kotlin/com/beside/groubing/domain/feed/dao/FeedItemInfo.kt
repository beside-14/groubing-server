package com.beside.groubing.domain.feed.dao

import com.querydsl.core.annotations.QueryProjection

class FeedItemInfo @QueryProjection constructor(
    val memberId: Long,

    val title: String
)
