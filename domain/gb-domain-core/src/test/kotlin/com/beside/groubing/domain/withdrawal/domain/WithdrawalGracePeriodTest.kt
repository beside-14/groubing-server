package com.beside.groubing.domain.withdrawal.domain

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class WithdrawalGracePeriodTest : BehaviorSpec({

    Given("탈퇴 유예기간(365일) 규칙이 있을 때") {
        When("만료 기준 시각을 계산하면") {
            val now = LocalDateTime.of(2026, 1, 1, 0, 0)

            Then("now 에서 유예기간을 뺀 시각을 반환한다.") {
                WithdrawalGracePeriod.calculateExpirationThreshold(now) shouldBe LocalDateTime.of(2025, 1, 1, 0, 0)
            }
        }
    }
})
