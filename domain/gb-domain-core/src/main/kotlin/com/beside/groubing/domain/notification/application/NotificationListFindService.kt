package com.beside.groubing.domain.notification.application

import com.beside.groubing.domain.bingo.domain.port.BingoBoardQueryRepository
import com.beside.groubing.domain.notification.domain.NotificationItem
import com.beside.groubing.domain.notification.domain.port.NotificationQueryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class NotificationListFindService(
    private val notificationQueryRepository: NotificationQueryRepository,
    private val bingoBoardQueryRepository: BingoBoardQueryRepository
) {
    fun findNotifications(memberId: Long): List<NotificationItem> {
        val bingoBoardIds = bingoBoardQueryRepository.findAllOf(memberId).map { it.id }
        return notificationQueryRepository.findRecentOf(bingoBoardIds, memberId)
    }
}
