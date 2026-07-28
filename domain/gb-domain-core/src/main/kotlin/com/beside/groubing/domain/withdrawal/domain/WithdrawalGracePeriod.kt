package com.beside.groubing.domain.withdrawal.domain

import java.time.LocalDateTime

object WithdrawalGracePeriod {
    const val GRACE_DAYS = 365L

    fun calculateExpirationThreshold(now: LocalDateTime): LocalDateTime = now.minusDays(GRACE_DAYS)
}
