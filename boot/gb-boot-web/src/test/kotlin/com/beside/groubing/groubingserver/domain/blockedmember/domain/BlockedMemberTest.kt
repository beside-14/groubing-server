package com.beside.groubing.groubingserver.domain.blockedmember.domain

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.single

class BlockedMemberTest : FunSpec({
    val requesterId = Arb.long(1L..10L).single()
    val targetMemberId = Arb.long(11L..20L).single()
    val blockedMember = BlockedMember.create(requesterId, targetMemberId)

    context("차단 당사자 여부를 테스트한다.") {
        test("차단 당사자인 경우 참을 반환한다.") {
            blockedMember.isBlockedMember(requesterId) shouldBe true
        }

        test("차단 당사자가 아닌 경우 거짓을 반환한다.") {
            blockedMember.isBlockedMember(targetMemberId) shouldBe false
        }
    }
})
