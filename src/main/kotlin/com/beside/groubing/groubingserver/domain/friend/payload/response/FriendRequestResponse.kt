package com.beside.groubing.groubingserver.domain.friend.payload.response

import com.beside.groubing.groubingserver.domain.friend.domain.Friend
import com.beside.groubing.groubingserver.domain.friend.domain.FriendStatus

data class FriendRequestResponse(
    val id: Long,
    val memberId: Long,
    val email: String?,
    val nickname: String,
    val profileUrl: String?,
    val status: FriendStatus
) {
    constructor(friendRequest: Friend) : this(
        friendRequest.id,
        friendRequest.inviter.id,
        friendRequest.inviter.email,
        friendRequest.inviter.nickname,
        friendRequest.inviter.profile?.url,
        friendRequest.status
    )
}
