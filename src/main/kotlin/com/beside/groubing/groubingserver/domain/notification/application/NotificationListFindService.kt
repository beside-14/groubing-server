package com.beside.groubing.groubingserver.domain.notification.application

import com.beside.groubing.groubingserver.domain.bingo.dao.BingoBoardListFindDao
import com.beside.groubing.groubingserver.domain.notification.domain.NotificationRepository
import com.beside.groubing.groubingserver.domain.notification.payload.response.NotificationResponse
import com.beside.groubing.groubingserver.global.response.PageResponse
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class NotificationListFindService(
    private val notificationRepository: NotificationRepository,

    private val bingoBoardListFindDao: BingoBoardListFindDao
) {
    fun findNotifications(memberId: Long, pageable: Pageable): PageResponse<NotificationResponse> {
        val bingoBoardIds = bingoBoardListFindDao.findBingoBoardList(memberId)
            .map { it.id }
        val notifications = notificationRepository.findByBingoBoardIdInAndMemberIdNotOrderByCreatedDateDesc(bingoBoardIds, memberId, pageable)
        return PageResponse(notifications.map { NotificationResponse.create(it) })
    }
}
