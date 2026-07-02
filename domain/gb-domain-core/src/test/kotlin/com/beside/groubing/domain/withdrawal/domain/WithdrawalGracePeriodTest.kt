package com.beside.groubing.domain.withdrawal.domain

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class WithdrawalGracePeriodTest : BehaviorSpec({

    Given("유예기간 365일이 주어졌을 때") {
        val gracePeriod = WithdrawalGracePeriod(days = 365)

        When("만료 기준 시각을 계산하면") {
            val now = LocalDateTime.of(2026, 1, 1, 0, 0)

            Then("now 에서 유예기간을 뺀 시각을 반환한다.") {
                gracePeriod.calculateExpirationThreshold(now) shouldBe LocalDateTime.of(2025, 1, 1, 0, 0)
            }
        }
    }

    Given("유예기간이 0일 이하일 때") {
        When("생성하면") {
            Then("IllegalArgumentException 이 발생한다.") {
                shouldThrow<IllegalArgumentException> {
                    WithdrawalGracePeriod(days = 0)
                }.message shouldBe "탈퇴 유예기간은 1일 이상이어야 합니다. days: 0"
            }
        }
    }
})
