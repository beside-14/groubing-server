package com.beside.groubing.domain.withdrawal.application

import com.beside.groubing.domain.member.domain.port.MemberQueryRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ExpiredMemberCleanupService(
    private val memberQueryRepository: MemberQueryRepository,
    private val memberCleanupExecutor: MemberCleanupExecutor,
    @Value("\${withdrawal.grace-days:365}") private val graceDays: Long
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun cleanupExpired(now: LocalDateTime = LocalDateTime.now()) {
        val threshold = now.minusDays(graceDays)
        memberQueryRepository.findExpiredMemberIds(threshold).forEach { memberId ->
            runCatching { memberCleanupExecutor.cleanup(memberId) }
                .onFailure { log.error("만료 회원 정리 실패. memberId=$memberId", it) }
        }
    }
}
