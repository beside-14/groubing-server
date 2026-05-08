package com.beside.groubing.domain.friend.domain

import com.beside.groubing.domain.friend.exception.FriendInputException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class FriendRelationsTest : BehaviorSpec({
    val inviterId = 1L
    val inviteeId = 2L

    Given("빈 친구 관계") {
        val relations = FriendRelations.of(emptyList())

        When("findRependable 호출") {
            Then("FriendInputException - 다시 요청할 기록 없음") {
                shouldThrow<FriendInputException> { relations.findRependable(inviterId, inviteeId) }
            }
        }
    }

    Given("PENDING 상태의 친구 관계") {
        val pending = Friend.of(1L, inviterId, inviteeId, FriendStatus.PENDING)
        val relations = FriendRelations.of(listOf(pending))

        When("findRependable 호출") {
            Then("FriendInputException - 이미 대기 상태") {
                shouldThrow<FriendInputException> { relations.findRependable(inviterId, inviteeId) }
            }
        }
    }

    Given("ACCEPT 상태의 친구 관계") {
        val accepted = Friend.of(1L, inviterId, inviteeId, FriendStatus.ACCEPT)
        val relations = FriendRelations.of(listOf(accepted))

        When("findRependable 호출") {
            Then("FriendInputException - 이미 등록된 친구") {
                shouldThrow<FriendInputException> { relations.findRependable(inviterId, inviteeId) }
            }
        }
    }

    Given("inviter→invitee 방향으로 REJECT 된 기록") {
        val rejected = Friend.of(1L, inviterId, inviteeId, FriendStatus.REJECT)
        val relations = FriendRelations.of(listOf(rejected))

        When("findRependable(inviterId, inviteeId) 호출") {
            Then("해당 Friend 를 반환한다") {
                val result = relations.findRependable(inviterId, inviteeId)
                result.id shouldBe 1L
                result.status shouldBe FriendStatus.REJECT
            }
        }
    }

    Given("REJECT 된 기록이 있지만 방향이 반대") {
        val rejectedReversed = Friend.of(1L, inviteeId, inviterId, FriendStatus.REJECT)
        val relations = FriendRelations.of(listOf(rejectedReversed))

        When("findRependable(inviterId, inviteeId) 호출") {
            Then("FriendInputException - 매칭되는 기록 없음") {
                shouldThrow<FriendInputException> { relations.findRependable(inviterId, inviteeId) }
            }
        }
    }
})
