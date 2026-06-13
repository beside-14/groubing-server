package com.beside.groubing.domain.bingo.domain

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class BingoMemberTest : BehaviorSpec({

    Given("BingoMember.create") {
        When("memberType 을 지정하지 않고 생성하면") {
            val member = BingoMember.create(memberId = 1L)

            Then("PARTICIPANT 타입으로 활성 상태로 생성된다.") {
                member.bingoMemberType shouldBe BingoMemberType.PARTICIPANT
                member.isLeader() shouldBe false
                member.active shouldBe true
            }
        }

        When("LEADER 타입으로 생성하면") {
            val member = BingoMember.create(memberId = 1L, bingoMemberType = BingoMemberType.LEADER)

            Then("isLeader 가 true 다.") {
                member.isLeader() shouldBe true
            }
        }
    }

    Given("inactive") {
        When("활성 멤버를 비활성화하면") {
            val member = BingoMember.create(memberId = 1L)
            member.inactive()

            Then("active 가 false 가 된다.") {
                member.active shouldBe false
            }
        }
    }
})
