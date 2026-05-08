package com.beside.groubing.domain.notification.payload.response

import com.beside.groubing.domain.notification.domain.NotificationItem
import com.beside.groubing.global.domain.file.domain.FileInfo

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
                profileUrl = FileInfo.urlOfOrNull(item.profileFileName)
            )
        }
    }
}
