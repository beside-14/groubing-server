package com.beside.groubing.groubingserver.domain.notification.domain

import com.querydsl.core.annotations.QueryProjection

class NotificationWithMemberProfile @QueryProjection constructor(
    val bingoBoardId: Long,

    val memberId: Long,

    val message: String,

    val profileUrl: String?
)
