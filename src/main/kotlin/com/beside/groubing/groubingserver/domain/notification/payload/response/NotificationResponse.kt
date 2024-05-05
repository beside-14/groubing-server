package com.beside.groubing.groubingserver.domain.notification.payload.response

import com.beside.groubing.groubingserver.domain.notification.domain.NotificationWithMemberProfile

class NotificationResponse(
    val bingoBoardId: Long,

    val memberId: Long,

    val message: String,

    val profileUrl: String?
)
{
    companion object {
        fun create(notification: NotificationWithMemberProfile): NotificationResponse {
            return NotificationResponse(
                bingoBoardId = notification.bingoBoardId,
                memberId = notification.memberId,
                message = notification.message,
                profileUrl = "/api/files/${notification.profileUrl}"
            )
        }
    }
}
