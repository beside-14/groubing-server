package com.beside.groubing.groubingserver.domain.notification.domain

class Notification private constructor(
    val id: Long,
    val bingoBoardId: Long,
    val memberId: Long,
    val message: String
) {
    companion object {
        fun create(bingoBoardId: Long, memberId: Long, message: String): Notification {
            return Notification(id = 0L, bingoBoardId = bingoBoardId, memberId = memberId, message = message)
        }

        fun of(id: Long, bingoBoardId: Long, memberId: Long, message: String): Notification {
            return Notification(id = id, bingoBoardId = bingoBoardId, memberId = memberId, message = message)
        }
    }
}
