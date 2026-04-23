package com.beside.groubing.groubingserver.domain.friend.payload.response

import com.beside.groubing.groubingserver.domain.friend.domain.Friend
import com.beside.groubing.groubingserver.domain.friend.domain.FriendStatus
import com.beside.groubing.groubingserver.domain.member.domain.Member

data class FriendRequestResponse(
    val id: Long,
    val memberId: Long,
    val email: String?,
    val nickname: String,
    val profileUrl: String?,
    val status: FriendStatus
) {
    private constructor(id: Long, status: FriendStatus, member: Member) : this(
        id,
        member.id,
        member.email,
        member.nickname,
        member.profile?.url,
        status
    )

    companion object {
        fun forReceived(receivedFriendRequest: Friend): FriendRequestResponse =
            FriendRequestResponse(receivedFriendRequest.id, receivedFriendRequest.status, receivedFriendRequest.inviter)

        fun forSend(sendFriendRequest: Friend): FriendRequestResponse =
            FriendRequestResponse(sendFriendRequest.id, sendFriendRequest.status, sendFriendRequest.invitee)
    }
}
