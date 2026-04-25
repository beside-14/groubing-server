package com.beside.groubing.domain.notification.entity

import com.beside.groubing.domain.notification.domain.Notification
import com.beside.groubing.global.domain.jpa.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "NOTIFICATIONS")
class NotificationEntity(
    val bingoBoardId: Long,

    val memberId: Long,

    val message: String
) : BaseEntity() {
    @Id
    @Column(name = "NOTIFICATION_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L

    fun toDomain(): Notification = Notification.of(
        id = id,
        bingoBoardId = bingoBoardId,
        memberId = memberId,
        message = message
    )

    companion object {
        fun from(notification: Notification): NotificationEntity = NotificationEntity(
            bingoBoardId = notification.bingoBoardId,
            memberId = notification.memberId,
            message = notification.message
        )
    }
}
