package com.beside.groubing.domain.friend.domain

class FriendMember(
    val friendId: Long,
    val memberId: Long,
    val nickname: String,
    val profileFileName: String?,
    val status: FriendStatus
)
