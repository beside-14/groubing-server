package com.beside.groubing.domain.feed.domain

class FeedEntry(
    val memberId: Long,
    val nickname: String,
    val profileUrl: String?,
    val itemTitles: List<String>,
    val isFriendRequestReceived: Boolean,
    val isFriendRequestSent: Boolean
) {
    companion object {
        const val MAX_FEED_ITEMS = 5
    }
}
