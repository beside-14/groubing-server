package com.beside.groubing.domain.withdrawal.application

import com.beside.groubing.domain.member.domain.port.MemberQueryRepository
import com.beside.groubing.domain.withdrawal.domain.WithdrawalGracePeriod
import mu.KotlinLogging
import org.springframework.stereotype.Service
import java.time.LocalDateTime

private val log = KotlinLogging.logger {}

@Service
class ExpiredMemberCleanupService(
    private val memberQueryRepository: MemberQueryRepository,
    private val memberCleaner: MemberCleaner,
    private val gracePeriod: WithdrawalGracePeriod
) {
    fun cleanupExpired(now: LocalDateTime = LocalDateTime.now()) {
        memberQueryRepository.findExpiredMemberIds(gracePeriod.calculateExpirationThreshold(now))
            .forEach { memberId ->
                runCatching { memberCleaner.cleanup(memberId) }
                    .onFailure { log.error(it) { "만료 회원 정리 실패. memberId=$memberId" } }
            }
    }
}
