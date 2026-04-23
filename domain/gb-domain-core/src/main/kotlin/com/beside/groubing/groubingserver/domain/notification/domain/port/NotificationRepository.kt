package com.beside.groubing.groubingserver.domain.notification.domain.port

import com.beside.groubing.groubingserver.domain.notification.domain.Notification

interface NotificationRepository {
    fun save(notification: Notification): Notification
}
