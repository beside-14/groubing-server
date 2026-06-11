package com.beside.groubing.domain.feed.payload.response

import com.beside.groubing.domain.feed.domain.FeedEntry
import com.beside.groubing.domain.common.id.ObfuscationType
import com.beside.groubing.global.id.EncryptId

class FeedResponse private constructor(
    @EncryptId(ObfuscationType.MEMBER)
    val memberId: Long,

    val nickname: String,

    val profile: String?,

    val feedItems: List<FeedItemDto>,

    val isFriendRequestReceived: Boolean,

    val isFriendRequestSend: Boolean
) {
    class FeedItemDto(val title: String)

    companion object {
        fun of(entry: FeedEntry): FeedResponse = FeedResponse(
            memberId = entry.memberId,
            nickname = entry.nickname,
            profile = entry.profileUrl,
            feedItems = entry.itemTitles.map { FeedItemDto(it) },
            isFriendRequestReceived = entry.isFriendRequestReceived,
            isFriendRequestSend = entry.isFriendRequestSent
        )
    }
}
