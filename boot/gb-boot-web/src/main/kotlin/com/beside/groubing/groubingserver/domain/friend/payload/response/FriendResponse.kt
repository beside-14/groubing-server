package com.beside.groubing.groubingserver.domain.friend.payload.response

import com.beside.groubing.groubingserver.domain.member.entity.MemberEntity

data class FriendResponse(
    val id: Long,
    val memberId: Long,
    val email: String?,
    val nickname: String,
    val profileUrl: String?
) {
    constructor(idToFriend: Map.Entry<Long, MemberEntity>) : this(
        idToFriend.key,
        idToFriend.value.id,
        idToFriend.value.email,
        idToFriend.value.nickname,
        idToFriend.value.profile?.url
    )
}
