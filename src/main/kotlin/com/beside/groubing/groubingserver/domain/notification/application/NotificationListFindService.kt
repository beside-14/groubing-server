package com.beside.groubing.groubingserver.domain.notification.application

import com.beside.groubing.groubingserver.domain.bingo.dao.BingoBoardListFindDao
import com.beside.groubing.groubingserver.domain.notification.dao.NotificationFindDao
import com.beside.groubing.groubingserver.domain.notification.payload.response.NotificationResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class NotificationListFindService(
    private val notificationFindDao: NotificationFindDao,

    private val bingoBoardListFindDao: BingoBoardListFindDao
) {
    fun findNotifications(memberId: Long): List<NotificationResponse> {
        val bingoBoardIds = bingoBoardListFindDao.findBingoBoardList(memberId)
            .map { it.id }
        val notifications = notificationFindDao.findNotifications(bingoBoardIds, memberId)
        return notifications.map { NotificationResponse.create(it) }
    }
}
