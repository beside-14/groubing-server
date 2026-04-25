package com.beside.groubing.domain.friend.payload.response

import com.beside.groubing.domain.friend.domain.FriendMember

data class FriendResponse(
    val id: Long,
    val memberId: Long,
    val email: String?,
    val nickname: String,
    val profileUrl: String?
) {
    companion object {
        fun of(friendMember: FriendMember): FriendResponse = FriendResponse(
            id = friendMember.friendId,
            memberId = friendMember.memberId,
            email = friendMember.email,
            nickname = friendMember.nickname,
            profileUrl = friendMember.profileFileName?.let { "/api/files/$it" }
        )
    }
}
