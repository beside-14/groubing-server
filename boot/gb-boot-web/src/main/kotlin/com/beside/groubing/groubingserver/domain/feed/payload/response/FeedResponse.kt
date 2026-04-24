package com.beside.groubing.groubingserver.domain.feed.payload.response

import com.beside.groubing.groubingserver.domain.friend.dao.FriendMemberInfo
import com.beside.groubing.groubingserver.domain.member.domain.Member

class FeedResponse private constructor(
    val memberId: Long,

    val nickname: String,

    val profile: String?,

    val feedItems: List<FeedItemDto>,
) {
    var isFriendRequestReceived: Boolean = false

    var isFriendRequestSend: Boolean = false

    fun checkFriendRequest(
        friendRequestReceivedList: List<FriendMemberInfo>,
        friendRequestSendList: List<FriendMemberInfo>
    ) {
        isFriendRequestReceived = friendRequestReceivedList.any { it.memberId == memberId }
        isFriendRequestSend = friendRequestSendList.any { it.memberId == memberId }
    }

    class FeedItemDto(val title: String)

    companion object {
        fun of(member: Member, itemTitles: List<String>): FeedResponse =
            FeedResponse(
                memberId = member.id,
                nickname = member.nickname,
                profile = member.profileUrl,
                feedItems = itemTitles.map { FeedItemDto(it) }
            )
    }
}
