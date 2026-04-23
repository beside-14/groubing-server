package com.beside.groubing.groubingserver.domain.member.api

import com.beside.groubing.groubingserver.domain.member.application.MemberNotiUpdateService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/members")
class MemberNotiUpdateApi(
    private val memberNotiUpdateService: MemberNotiUpdateService
) {
    @PatchMapping("/notification-on")
    fun onNotification(@AuthenticationPrincipal memberId: Long) {
        memberNotiUpdateService.onNotification(memberId)
    }

    @PatchMapping("/notification-off")
    fun offNotification(@AuthenticationPrincipal memberId: Long) {
        memberNotiUpdateService.offNotification(memberId)
    }

}
