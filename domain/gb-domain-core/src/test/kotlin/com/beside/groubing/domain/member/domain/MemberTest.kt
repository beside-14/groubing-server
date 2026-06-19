package com.beside.groubing.domain.member.domain

import com.beside.groubing.domain.member.exception.MemberInputException
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class MemberTest : BehaviorSpec({

    fun aMember(
        active: Boolean = true,
        deletedAt: LocalDateTime? = null,
        nickname: String = "nickname",
        profileUrl: String? = null
    ) = Member(
        id = 1L,
        loginId = "groubing",
        password = "encoded",
        nickname = nickname,
        role = MemberRole.MEMBER,
        memberType = MemberType.CLASSIC,
        fcmToken = null,
        notificationReceive = true,
        active = active,
        deletedAt = deletedAt,
        profileUrl = profileUrl
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

    Given("탈퇴한 회원(deletedAt 존재)이 주어졌을 때") {
        val member = aMember(
            active = false,
            deletedAt = LocalDateTime.now(),
            nickname = "원래닉네임",
            profileUrl = "https://image/profile.png"
        )

        When("anonymizeIfWithdrawn 을 호출하면") {
            val anonymized = member.anonymizeIfWithdrawn()

            Then("닉네임이 '탈퇴한 회원'으로 바뀌고 프로필이 제거된다") {
                anonymized.nickname shouldBe "탈퇴한 회원"
                anonymized.profileUrl shouldBe null
            }
        }
    }

    Given("활성 회원(deletedAt 없음)이 주어졌을 때") {
        val member = aMember(nickname = "원래닉네임", profileUrl = "https://image/profile.png")

        When("anonymizeIfWithdrawn 을 호출하면") {
            val result = member.anonymizeIfWithdrawn()

            Then("닉네임과 프로필이 그대로 유지된다") {
                result.nickname shouldBe "원래닉네임"
                result.profileUrl shouldBe "https://image/profile.png"
            }
        }
    }
})
