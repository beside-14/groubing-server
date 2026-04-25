package com.beside.groubing.domain.friend.domain

class FriendMember(
    val friendId: Long,
    val memberId: Long,
    val email: String?,
    val nickname: String,
    val profileFileName: String?,
    val status: FriendStatus
)
