package com.beside.groubing.domain.withdrawal.scheduler

import com.beside.groubing.domain.withdrawal.application.ExpiredMemberCleanupService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ExpiredMemberCleanupScheduler(
    private val expiredMemberCleanupService: ExpiredMemberCleanupService
) {
    @Scheduled(cron = "\${withdrawal.cleanup-cron:0 0 4 * * *}")
    fun cleanupExpiredMembers() {
        expiredMemberCleanupService.cleanupExpired()
    }
}
