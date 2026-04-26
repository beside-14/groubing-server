package com.beside.groubing.domain.friend.payload.response

import com.beside.groubing.domain.friend.domain.FriendMember
import com.beside.groubing.domain.friend.domain.FriendStatus
import com.beside.groubing.global.domain.file.domain.FileInfo

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
            profileUrl = FileInfo.urlOfOrNull(friendMember.profileFileName),
            status = friendMember.status
        )
    }
}
