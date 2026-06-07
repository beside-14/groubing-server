package com.beside.groubing.domain.friend.dao

import com.beside.groubing.domain.friend.domain.FriendStatus
import com.querydsl.core.annotations.QueryProjection

class FriendMemberInfo @QueryProjection constructor(
    val friendId: Long,

    val memberId: Long,

    val nickname: String,

    val profileFileName: String?,

    val status: FriendStatus
)
