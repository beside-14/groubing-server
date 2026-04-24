package com.beside.groubing.groubingserver.domain.notification.payload.response

import com.beside.groubing.groubingserver.domain.notification.domain.NotificationItem

class NotificationResponse(
    val bingoBoardId: Long,

    val memberId: Long,

    val message: String,

    val profileUrl: String?
) {
    companion object {
        fun of(item: NotificationItem): NotificationResponse {
            return NotificationResponse(
                bingoBoardId = item.bingoBoardId,
                memberId = item.memberId,
                message = item.message,
                profileUrl = item.profileFileName?.let { "/api/files/$it" }
            )
        }
    }
}
