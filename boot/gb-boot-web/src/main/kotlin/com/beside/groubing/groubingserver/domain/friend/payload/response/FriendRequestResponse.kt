package com.beside.groubing.groubingserver.domain.friend.payload.response

import com.beside.groubing.groubingserver.domain.friend.domain.FriendMember
import com.beside.groubing.groubingserver.domain.friend.domain.FriendStatus

data class FriendRequestResponse(
    val id: Long,
    val memberId: Long,
    val email: String?,
    val nickname: String,
    val profileUrl: String?,
    val status: FriendStatus
) {
    companion object {
        fun of(friendMember: FriendMember): FriendRequestResponse = FriendRequestResponse(
            id = friendMember.friendId,
            memberId = friendMember.memberId,
            email = friendMember.email,
            nickname = friendMember.nickname,
            profileUrl = friendMember.profileFileName?.let { "/api/files/$it" },
            status = friendMember.status
        )
    }
}
