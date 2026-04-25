package com.beside.groubing.domain.notification.api

import com.beside.groubing.domain.notification.application.NotificationListFindService
import com.beside.groubing.domain.notification.payload.response.NotificationResponse
import com.beside.groubing.global.response.ApiResponse
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
    fun findNotifications(@AuthenticationPrincipal memberId: Long): ApiResponse<List<NotificationResponse>> {
        val responses = notificationListFindService.findNotifications(memberId).map(NotificationResponse::of)
        return ApiResponse.OK(responses)
    }
}
