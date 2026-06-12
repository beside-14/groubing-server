package com.beside.groubing.domain.blockedmember.domain

import com.beside.groubing.domain.blockedmember.domain.port.BlockedMemberRepository
import com.beside.groubing.domain.blockedmember.exception.BlockedMemberInputException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class BlockMemberValidatorTest : BehaviorSpec({
    val blockedMemberRepository = mockk<BlockedMemberRepository>()
    val validator = BlockMemberValidator(blockedMemberRepository)

    val requesterId = 1L
    val targetMemberId = 2L

    Given("아직 차단하지 않은 회원일 때") {
        every { blockedMemberRepository.exists(requesterId, targetMemberId) } returns false

        When("validate 호출") {
            Then("예외가 발생하지 않는다") {
                validator.validate(requesterId, targetMemberId)
            }
        }
    }

    Given("이미 차단한 회원일 때") {
        every { blockedMemberRepository.exists(requesterId, targetMemberId) } returns true

        When("validate 호출") {
            Then("BlockedMemberInputException 이 발생한다") {
                shouldThrow<BlockedMemberInputException> {
                    validator.validate(requesterId, targetMemberId)
                }.message shouldBe "이미 차단한 유저입니다."
            }
        }
    }
})
