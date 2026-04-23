package com.beside.groubing.groubingserver.domain.notification.repository

import com.beside.groubing.groubingserver.domain.notification.domain.Notification
import com.beside.groubing.groubingserver.domain.notification.domain.port.NotificationRepository
import com.beside.groubing.groubingserver.domain.notification.entity.NotificationEntity
import org.springframework.stereotype.Repository

@Repository
class NotificationRepositoryAdapter(
    private val notificationJpaRepository: NotificationJpaRepository
) : NotificationRepository {
    override fun save(notification: Notification): Notification {
        return notificationJpaRepository.save(NotificationEntity.from(notification)).toDomain()
    }
}
