package com.beside.groubing.domain.bingo.domain

import com.beside.groubing.domain.bingo.domain.port.BingoBoardQueryRepository
import com.beside.groubing.domain.bingo.exception.BingoIllegalStateException
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class BingoLeaderValidatorTest : BehaviorSpec({
    val bingoBoardQueryRepository = mockk<BingoBoardQueryRepository>()
    val validator = BingoLeaderValidator(bingoBoardQueryRepository)

    val bingoBoardId = 2L
    val memberId = 1L

    Given("해당 회원이 빙고의 리더일 때") {
        every { bingoBoardQueryRepository.isLeaderOf(bingoBoardId, memberId) } returns true

        When("validate 호출") {
            Then("예외가 발생하지 않는다") {
                shouldNotThrowAny {
                    validator.validate(bingoBoardId, memberId)
                }
            }
        }
    }

    Given("해당 회원이 빙고의 리더가 아닐 때") {
        every { bingoBoardQueryRepository.isLeaderOf(bingoBoardId, memberId) } returns false

        When("validate 호출") {
            Then("BingoIllegalStateException 이 발생한다") {
                shouldThrow<BingoIllegalStateException> {
                    validator.validate(bingoBoardId, memberId)
                }.message shouldBe "해당 빙고를 수정할 권한이 없습니다."
            }
        }
    }
})
