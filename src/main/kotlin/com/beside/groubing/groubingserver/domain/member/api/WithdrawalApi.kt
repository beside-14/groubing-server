package com.beside.groubing.groubingserver.domain.member.api

import com.beside.groubing.groubingserver.domain.member.application.WithdrawalService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/members")
class WithdrawalApi(
    private val withdrawalService: WithdrawalService
) {

    @PostMapping("/withdrawal")
    fun withdrawal(@AuthenticationPrincipal memberId: Long) {
        withdrawalService.withdrawal(memberId)
    }
}
