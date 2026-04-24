package com.beside.groubing.groubingserver.domain.friend.payload.response

import com.beside.groubing.groubingserver.domain.friend.dao.FriendMemberInfo
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
        fun of(info: FriendMemberInfo): FriendRequestResponse = FriendRequestResponse(
            id = info.friendId,
            memberId = info.memberId,
            email = info.email,
            nickname = info.nickname,
            profileUrl = info.profileFileName?.let { "/api/files/$it" },
            status = info.status
        )
    }
}
