package com.beside.groubing.domain.blockedmember.dao

import com.querydsl.core.annotations.QueryProjection

class BlockedMemberTargetInfo @QueryProjection constructor(
    val id: Long,

    val nickname: String,

    val profileFileName: String?
)
