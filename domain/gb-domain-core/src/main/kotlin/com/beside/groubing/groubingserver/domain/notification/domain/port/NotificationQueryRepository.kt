package com.beside.groubing.groubingserver.domain.notification.domain.port

import com.beside.groubing.groubingserver.domain.notification.domain.NotificationItem

interface NotificationQueryRepository {
    fun findRecentOf(bingoBoardIds: List<Long>, myMemberId: Long): List<NotificationItem>
}
