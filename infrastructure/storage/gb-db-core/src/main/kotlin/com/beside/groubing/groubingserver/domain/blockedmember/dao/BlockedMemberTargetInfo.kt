package com.beside.groubing.groubingserver.domain.blockedmember.dao

import com.querydsl.core.annotations.QueryProjection

class BlockedMemberTargetInfo @QueryProjection constructor(
    val id: Long,

    val email: String?,

    val nickname: String,

    val profileFileName: String?
)
