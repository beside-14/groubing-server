package com.beside.groubing.groubingserver.domain.notification.domain

import com.beside.groubing.groubingserver.global.domain.jpa.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "NOTIFICATIONS")
class Notification internal constructor(
    val bingoBoardId: Long,

    val memberId: Long,

    val message: String
) : BaseEntity()
{
    @Id
    @Column(name = "NOTIFICATION_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L
}
