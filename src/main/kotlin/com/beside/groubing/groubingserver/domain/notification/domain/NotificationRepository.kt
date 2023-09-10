package com.beside.groubing.groubingserver.domain.notification.domain

import org.springframework.data.jpa.repository.JpaRepository

interface NotificationRepository: JpaRepository<Notification, Long>
