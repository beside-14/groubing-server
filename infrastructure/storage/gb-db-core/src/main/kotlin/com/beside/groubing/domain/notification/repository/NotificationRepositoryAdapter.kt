package com.beside.groubing.domain.notification.repository

import com.beside.groubing.domain.notification.dao.NotificationFindDao
import com.beside.groubing.domain.notification.domain.Notification
import com.beside.groubing.domain.notification.domain.NotificationItem
import com.beside.groubing.domain.notification.domain.port.NotificationQueryRepository
import com.beside.groubing.domain.notification.domain.port.NotificationRepository
import com.beside.groubing.domain.notification.entity.NotificationEntity
import org.springframework.stereotype.Repository

@Repository
class NotificationRepositoryAdapter(
    private val notificationJpaRepository: NotificationJpaRepository,
    private val notificationFindDao: NotificationFindDao
) : NotificationRepository, NotificationQueryRepository {
    override fun save(notification: Notification): Notification {
        return notificationJpaRepository.save(NotificationEntity.from(notification)).toDomain()
    }

    override fun deleteAllByMemberId(memberId: Long) {
        notificationJpaRepository.deleteByMemberId(memberId)
    }

    override fun deleteAllByBingoBoardId(bingoBoardId: Long) {
        notificationJpaRepository.deleteByBingoBoardId(bingoBoardId)
    }

    override fun findRecentOf(bingoBoardIds: List<Long>, myMemberId: Long): List<NotificationItem> {
        return notificationFindDao.findNotifications(bingoBoardIds, myMemberId)
            .map {
                NotificationItem(
                    bingoBoardId = it.bingoBoardId,
                    memberId = it.memberId,
                    message = it.message,
                    profileFileName = it.profileUrl
                )
            }
    }
}
