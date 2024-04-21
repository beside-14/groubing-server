package com.beside.groubing.groubingserver.domain.notification.payload.response

import com.beside.groubing.groubingserver.domain.notification.domain.Notification

class NotificationResponse(
    val bingoBoardId: Long,

    val memberId: Long,

    val message: String
)
{
    companion object {
        fun create(notification: Notification): NotificationResponse {
            return NotificationResponse(
                bingoBoardId = notification.bingoBoardId,
                memberId = notification.memberId, message = notification.message
            )
        }
    }
}
