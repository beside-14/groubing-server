package com.beside.groubing.domain.member.domain

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class MembersTest : BehaviorSpec({

    fun aMember(
        id: Long,
        nickname: String,
        active: Boolean,
        deletedAt: LocalDateTime?,
        profileUrl: String?
    ) = Member(
        id = id,
        loginId = "login$id",
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

    Given("탈퇴 회원과 활성 회원이 섞인 Members 가 주어졌을 때") {
        val active = aMember(1L, "활성유저", active = true, deletedAt = null, profileUrl = "p1.png")
        val withdrawn = aMember(2L, "탈퇴전닉", active = false, deletedAt = LocalDateTime.now(), profileUrl = "p2.png")
        val members = Members.of(listOf(active, withdrawn))

        When("anonymizeWithdrawn 을 호출하면") {
            val anonymized = members.anonymizeWithdrawn().associateBy { it.id }

            Then("탈퇴 회원만 '탈퇴한 회원'으로 익명화되고 활성 회원은 그대로 유지된다") {
                anonymized[1L]!!.nickname shouldBe "활성유저"
                anonymized[1L]!!.profileUrl shouldBe "p1.png"
                anonymized[2L]!!.nickname shouldBe "탈퇴한 회원"
                anonymized[2L]!!.profileUrl shouldBe null
            }
        }
    }
})
