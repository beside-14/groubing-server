package com.beside.groubing.domain.withdrawal.domain

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class WithdrawalGracePeriod(
    @Value("\${withdrawal.grace-days:365}") private val days: Long
) {
    init {
        require(days > 0) { "탈퇴 유예기간은 1일 이상이어야 합니다. days: $days" }
    }

    fun calculateExpirationThreshold(now: LocalDateTime): LocalDateTime = now.minusDays(days)
}
