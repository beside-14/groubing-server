package com.beside.groubing.domain.bingo.domain

import com.beside.groubing.domain.member.domain.port.MemberQueryRepository
import com.beside.groubing.domain.member.exception.MemberInputException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class BingoBoardMembersValidatorTest : BehaviorSpec({
    val memberQueryRepository = mockk<MemberQueryRepository>()
    val validator = BingoBoardMembersValidator(memberQueryRepository)

    Given("입력된 모든 ID 가 존재하는 회원일 때") {
        val memberIds = listOf(1L, 2L, 3L)
        every { memberQueryRepository.count(memberIds) } returns memberIds.size

        When("validate 호출") {
            Then("예외가 발생하지 않는다") {
                validator.validate(memberIds)
            }
        }
    }

    Given("입력된 ID 중 존재하지 않는 회원이 있을 때") {
        val memberIds = listOf(1L, 2L, 3L)
        every { memberQueryRepository.count(memberIds) } returns memberIds.size - 1

        When("validate 호출") {
            Then("MemberInputException 이 발생한다") {
                shouldThrow<MemberInputException> {
                    validator.validate(memberIds)
                }.message shouldBe "입력된 ID 중 존재하지 않는 회원이 있습니다. memberIds:$memberIds"
            }
        }
    }

    Given("빈 memberIds 가 입력될 때") {
        val memberIds = emptyList<Long>()
        every { memberQueryRepository.count(memberIds) } returns 0

        When("validate 호출") {
            Then("count 와 size 가 모두 0 으로 일치하여 예외가 발생하지 않는다") {
                validator.validate(memberIds)
            }
        }
    }
})
