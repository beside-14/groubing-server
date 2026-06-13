package com.beside.groubing.domain.member.domain

import com.beside.groubing.domain.member.domain.port.MemberQueryRepository
import com.beside.groubing.domain.member.exception.MemberInputException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class NicknameUniquenessValidatorTest : BehaviorSpec({
    val memberQueryRepository = mockk<MemberQueryRepository>()
    val validator = NicknameUniquenessValidator(memberQueryRepository)

    val nickname = "그루빙"

    Given("닉네임이 중복되지 않을 때") {
        every { memberQueryRepository.existsByNickname(nickname) } returns false

        When("validate 호출") {
            Then("예외가 발생하지 않는다") {
                validator.validate(nickname)
            }
        }
    }

    Given("이미 사용 중인 닉네임일 때") {
        every { memberQueryRepository.existsByNickname(nickname) } returns true

        When("validate 호출") {
            Then("MemberInputException 이 발생한다") {
                shouldThrow<MemberInputException> {
                    validator.validate(nickname)
                }.message shouldBe "이미 사용 중인 닉네임 입니다."
            }
        }
    }
})
