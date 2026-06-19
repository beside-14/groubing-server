package com.beside.groubing.domain.withdrawal.application

import com.beside.groubing.domain.member.domain.port.MemberQueryRepository
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.Runs
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import java.time.LocalDateTime

class ExpiredMemberCleanupServiceTest : BehaviorSpec({
    val memberQueryRepository = mockk<MemberQueryRepository>()
    val memberCleanupExecutor = mockk<MemberCleanupExecutor>()
    val cleanupService = ExpiredMemberCleanupService(memberQueryRepository, memberCleanupExecutor, graceDays = 365)

    Given("만료 회원 3명 중 1명 정리가 실패할 때") {
        val now = LocalDateTime.of(2026, 1, 1, 0, 0)
        every { memberQueryRepository.findExpiredMemberIds(any()) } returns listOf(1L, 2L, 3L)
        every { memberCleanupExecutor.cleanup(1L) } just Runs
        every { memberCleanupExecutor.cleanup(2L) } throws RuntimeException("boom")
        every { memberCleanupExecutor.cleanup(3L) } just Runs

        When("cleanupExpired 를 호출하면") {
            cleanupService.cleanupExpired(now)

            Then("한 명의 실패가 격리되고 나머지 회원도 모두 정리가 시도된다") {
                verify(exactly = 1) { memberCleanupExecutor.cleanup(1L) }
                verify(exactly = 1) { memberCleanupExecutor.cleanup(2L) }
                verify(exactly = 1) { memberCleanupExecutor.cleanup(3L) }
            }
        }
    }

    Given("그레이스 기간(365일) 회원이 주어졌을 때") {
        val now = LocalDateTime.of(2026, 1, 1, 0, 0)
        every { memberQueryRepository.findExpiredMemberIds(any()) } returns emptyList()

        When("cleanupExpired(now) 를 호출하면") {
            cleanupService.cleanupExpired(now)

            Then("now 에서 graceDays 를 뺀 시각을 만료 기준으로 스캔한다") {
                verify(exactly = 1) { memberQueryRepository.findExpiredMemberIds(now.minusDays(365)) }
            }
        }
    }
})
