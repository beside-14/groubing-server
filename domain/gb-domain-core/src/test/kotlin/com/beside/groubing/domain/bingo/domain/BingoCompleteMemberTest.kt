package com.beside.groubing.domain.bingo.domain

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class BingoCompleteMemberTest : BehaviorSpec({

    Given("BingoCompleteMember.create") {
        When("memberId 로 생성하면") {
            val completeMember = BingoCompleteMember.create(memberId = 1L)

            Then("활성 상태로 생성된다.") {
                completeMember.memberId shouldBe 1L
                completeMember.active shouldBe true
            }
        }
    }

    Given("inactive") {
        When("활성 완료 멤버를 비활성화하면") {
            val completeMember = BingoCompleteMember.create(memberId = 1L)
            completeMember.inactive()

            Then("active 가 false 가 된다.") {
                completeMember.active shouldBe false
            }
        }
    }
})
