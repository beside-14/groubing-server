package com.beside.groubing.groubingserver.domain.notification.domain

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface NotificationRepository: JpaRepository<Notification, Long> {
    fun findByBingoBoardIdInAndMemberIdNotOrderByCreatedDateDesc(bingoBoardIds: List<Long>, memberId: Long, pageable: Pageable): Page<Notification>
}
