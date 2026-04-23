package com.beside.groubing.groubingserver.domain.notification.repository

import com.beside.groubing.groubingserver.domain.notification.entity.NotificationEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface NotificationJpaRepository : JpaRepository<NotificationEntity, Long> {
    fun findByBingoBoardIdInAndMemberIdNotOrderByCreatedDateDesc(
        bingoBoardIds: List<Long>,
        memberId: Long,
        pageable: Pageable
    ): Page<NotificationEntity>
}
