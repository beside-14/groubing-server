package com.beside.groubing.groubingserver.domain.friendship.payload.response

import com.beside.groubing.groubingserver.domain.member.domain.Member

data class FriendResponse(
    val id: Long,
    val email: String,
    val nickname: String,
    val profileUrl: String?
) {
    constructor(friend: Member) : this(friend.id, friend.email, friend.nickname, friend.profile?.url)
}
