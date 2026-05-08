package com.beside.groubing.domain.friend.domain

import com.beside.groubing.domain.friend.exception.FriendInputException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class FriendTest : BehaviorSpec({
    val inviterId = 1L
    val inviteeId = 2L

    Given("Friend.create") {
        When("자기 자신에게 요청하는 경우") {
            Then("FriendInputException 발생") {
                shouldThrow<FriendInputException> {
                    Friend.create(inviterId, inviterId)
                }
            }
        }

        When("서로 다른 회원에게 요청하는 경우") {
            Then("PENDING 상태로 생성된다") {
                val friend = Friend.create(inviterId, inviteeId)
                friend.inviterId shouldBe inviterId
                friend.inviteeId shouldBe inviteeId
                friend.status shouldBe FriendStatus.PENDING
            }
        }
    }

    Given("PENDING 상태의 Friend") {
        val pending = Friend.of(1L, inviterId, inviteeId, FriendStatus.PENDING)

        When("invitee 가 accept 하면") {
            Then("ACCEPT 상태로 변경된다") {
                pending.accept(inviteeId).status shouldBe FriendStatus.ACCEPT
            }
        }

        When("invitee 가 reject 하면") {
            Then("REJECT 상태로 변경된다") {
                pending.reject(inviteeId).status shouldBe FriendStatus.REJECT
            }
        }

        When("inviter 가 accept 시도하면") {
            Then("FriendInputException 발생 - 당사자 아님") {
                shouldThrow<FriendInputException> { pending.accept(inviterId) }
            }
        }

        When("repend 시도하면") {
            Then("IllegalStateException - 거절된 요청만 다시 요청 가능") {
                shouldThrow<IllegalStateException> { pending.repend() }
            }
        }
    }

    Given("ACCEPT 상태의 Friend") {
        val accepted = Friend.of(1L, inviterId, inviteeId, FriendStatus.ACCEPT)

        When("accept 시도하면") {
            Then("FriendInputException 발생 - 이미 등록된 상태") {
                shouldThrow<FriendInputException> { accepted.accept(inviteeId) }
            }
        }
    }

    Given("REJECT 상태의 Friend") {
        val rejected = Friend.of(1L, inviterId, inviteeId, FriendStatus.REJECT)

        When("repend 하면") {
            Then("PENDING 상태로 변경된다") {
                rejected.repend().status shouldBe FriendStatus.PENDING
            }
        }
    }
})
