package com.beside.groubing.domain.notification.payload.response

import com.beside.groubing.domain.notification.domain.NotificationItem
import com.beside.groubing.global.domain.file.domain.FileInfo
import com.beside.groubing.global.domain.id.ObfuscationType
import com.beside.groubing.global.id.EncryptId

class NotificationResponse(
    @EncryptId(ObfuscationType.BINGO_BOARD)
    val bingoBoardId: Long,

    @EncryptId(ObfuscationType.MEMBER)
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
