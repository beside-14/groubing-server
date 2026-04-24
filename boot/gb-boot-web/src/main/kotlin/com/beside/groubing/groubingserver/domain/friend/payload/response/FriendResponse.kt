package com.beside.groubing.groubingserver.domain.friend.payload.response

import com.beside.groubing.groubingserver.domain.friend.dao.FriendMemberInfo

data class FriendResponse(
    val id: Long,
    val memberId: Long,
    val email: String?,
    val nickname: String,
    val profileUrl: String?
) {
    companion object {
        fun of(info: FriendMemberInfo): FriendResponse = FriendResponse(
            id = info.friendId,
            memberId = info.memberId,
            email = info.email,
            nickname = info.nickname,
            profileUrl = info.profileFileName?.let { "/api/files/$it" }
        )
    }
}
