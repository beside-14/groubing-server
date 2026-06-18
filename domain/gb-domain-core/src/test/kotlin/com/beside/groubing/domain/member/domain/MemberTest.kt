package com.beside.groubing.domain.member.domain

import com.beside.groubing.domain.member.exception.MemberInputException
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class MemberTest : BehaviorSpec({

    fun aMember(active: Boolean) = Member(
        id = 1L,
        loginId = "groubing",
        password = "encoded",
        nickname = "nickname",
        role = MemberRole.MEMBER,
        memberType = MemberType.CLASSIC,
        fcmToken = null,
        notificationReceive = true,
        active = active,
        deletedAt = null,
        profileUrl = null
    )

    Given("활성 상태의 회원이 주어졌을 때") {
        val member = aMember(active = true)

        When("validateWithdrawable 을 호출하면") {
            Then("예외가 발생하지 않는다") {
                shouldNotThrowAny { member.validateWithdrawable() }
            }
        }
    }

    Given("이미 탈퇴한 회원이 주어졌을 때") {
        val member = aMember(active = false)

        When("validateWithdrawable 을 호출하면") {
            Then("이미 탈퇴한 회원이라는 예외가 발생한다") {
                shouldThrow<MemberInputException> {
                    member.validateWithdrawable()
                }.message shouldBe "이미 탈퇴한 회원입니다."
            }
        }
    }
})
