package com.beside.groubing.domain.auth.domain

import com.beside.groubing.domain.member.domain.MemberRole
import com.beside.groubing.domain.member.domain.MemberType
import com.beside.groubing.domain.member.domain.NewMember
import com.beside.groubing.domain.member.domain.port.MemberQueryRepository
import com.beside.groubing.domain.member.exception.MemberInputException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class SignUpValidatorTest : BehaviorSpec({
    val memberQueryRepository = mockk<MemberQueryRepository>()
    val validator = SignUpValidator(memberQueryRepository)

    fun newMember(
        loginId: String? = "groubing",
        nickname: String = "그루빙"
    ) = NewMember(
        loginId = loginId,
        password = "password123",
        nickname = nickname,
        role = MemberRole.MEMBER,
        memberType = MemberType.CLASSIC
    )

    Given("loginId 와 nickname 모두 중복되지 않을 때") {
        val member = newMember()
        every { memberQueryRepository.existsByLoginId(member.loginId!!) } returns false
        every { memberQueryRepository.existsByNickname(member.nickname) } returns false

        When("validate 호출") {
            Then("예외가 발생하지 않는다") {
                validator.validate(member)
            }
        }
    }

    Given("loginId 가 null 일 때 (소셜 가입 등)") {
        val member = newMember(loginId = null)
        every { memberQueryRepository.existsByNickname(member.nickname) } returns false

        When("validate 호출") {
            Then("loginId 중복 검사를 건너뛰고 예외가 발생하지 않는다") {
                validator.validate(member)
            }
        }
    }

    Given("이미 사용 중인 loginId 일 때") {
        val member = newMember()
        every { memberQueryRepository.existsByLoginId(member.loginId!!) } returns true

        When("validate 호출") {
            Then("MemberInputException 이 발생한다") {
                shouldThrow<MemberInputException> {
                    validator.validate(member)
                }.message shouldBe "이미 사용 중인 아이디입니다."
            }
        }
    }

    Given("loginId 는 사용 가능하지만 nickname 이 이미 사용 중일 때") {
        val member = newMember()
        every { memberQueryRepository.existsByLoginId(member.loginId!!) } returns false
        every { memberQueryRepository.existsByNickname(member.nickname) } returns true

        When("validate 호출") {
            Then("MemberInputException 이 발생한다") {
                shouldThrow<MemberInputException> {
                    validator.validate(member)
                }.message shouldBe "이미 사용 중인 닉네임 입니다."
            }
        }
    }
})
