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
    private val memberCleaner: MemberCleaner
) {
    fun cleanupExpired(now: LocalDateTime = LocalDateTime.now()) {
        memberQueryRepository.findExpiredMemberIds(WithdrawalGracePeriod.calculateExpirationThreshold(now))
            .forEach { memberId ->
                runCatching { memberCleaner.cleanup(memberId) }
                    .onFailure { log.error(it) { "만료 회원 정리 실패. memberId=$memberId" } }
            }
    }
}
