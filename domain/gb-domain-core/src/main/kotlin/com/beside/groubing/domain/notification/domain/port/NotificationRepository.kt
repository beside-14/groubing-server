package com.beside.groubing.domain.notification.domain.port

import com.beside.groubing.domain.notification.domain.Notification

interface NotificationRepository {
    fun save(notification: Notification): Notification
}
