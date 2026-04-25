package com.beside.groubing.domain.notification.domain.port

import com.beside.groubing.domain.notification.domain.NotificationItem

interface NotificationQueryRepository {
    fun findRecentOf(bingoBoardIds: List<Long>, myMemberId: Long): List<NotificationItem>
}
