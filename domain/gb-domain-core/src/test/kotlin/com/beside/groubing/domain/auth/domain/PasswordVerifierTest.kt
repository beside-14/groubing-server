package com.beside.groubing.domain.auth.domain

import com.beside.groubing.domain.auth.domain.port.PasswordEncryptor
import com.beside.groubing.domain.member.exception.MemberInputException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class PasswordVerifierTest : BehaviorSpec({
    val passwordEncryptor = mockk<PasswordEncryptor>()
    val verifier = PasswordVerifier(passwordEncryptor)

    val rawPassword = "password123"
    val encodedPassword = "encoded-password"

    Given("입력한 비밀번호가 저장된 비밀번호와 일치할 때") {
        every { passwordEncryptor.matches(rawPassword, encodedPassword) } returns true

        When("verify 호출") {
            Then("예외가 발생하지 않는다") {
                verifier.verify(rawPassword, encodedPassword)
            }
        }
    }

    Given("입력한 비밀번호가 저장된 비밀번호와 일치하지 않을 때") {
        every { passwordEncryptor.matches(rawPassword, encodedPassword) } returns false

        When("verify 호출") {
            Then("MemberInputException 이 발생한다") {
                shouldThrow<MemberInputException> {
                    verifier.verify(rawPassword, encodedPassword)
                }.message shouldBe "비밀번호가 일치하지 않습니다."
            }
        }
    }
})
