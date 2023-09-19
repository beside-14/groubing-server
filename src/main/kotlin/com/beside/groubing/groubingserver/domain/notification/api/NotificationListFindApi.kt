package com.beside.groubing.groubingserver.domain.notification.api

import com.beside.groubing.groubingserver.domain.notification.application.NotificationListFindService
import com.beside.groubing.groubingserver.domain.notification.payload.response.NotificationResponse
import com.beside.groubing.groubingserver.global.response.ApiResponse
import com.beside.groubing.groubingserver.global.response.PageResponse
import org.springframework.data.domain.Pageable
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/notifications")
class NotificationListFindApi(
    private val notificationListFindService: NotificationListFindService
) {
    @GetMapping
    fun findNotifications(@AuthenticationPrincipal memberId: Long, pageable: Pageable): ApiResponse<PageResponse<NotificationResponse>> {
        val notifications = notificationListFindService.findNotifications(memberId, pageable)
        return ApiResponse.OK(notifications)
    }
}
