package com.beside.groubing.domain.bingo.domain

import com.beside.groubing.domain.bingo.exception.BingoInputException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class BingoGoalTest : BehaviorSpec({

    Given("3x3(최대 목표 8) 빙고 사이즈에서 BingoGoal 을 생성할 때") {
        val bingoSize = BingoSize.cache(3)

        When("최소값 1로 생성하면") {
            val bingoGoal = BingoGoal.create(1, bingoSize)

            Then("goal 이 1인 객체가 생성된다") {
                bingoGoal.goal shouldBe 1
            }
        }

        When("최대값(maxGoal) 8로 생성하면") {
            val bingoGoal = BingoGoal.create(bingoSize.getMaxGoal(), bingoSize)

            Then("goal 이 8인 객체가 생성된다") {
                bingoGoal.goal shouldBe 8
            }
        }

        When("0으로 생성하면") {
            Then("BingoInputException 이 발생한다") {
                shouldThrow<BingoInputException> {
                    BingoGoal.create(0, bingoSize)
                }
            }
        }

        When("음수로 생성하면") {
            Then("BingoInputException 이 발생한다") {
                shouldThrow<BingoInputException> {
                    BingoGoal.create(-1, bingoSize)
                }
            }
        }

        When("최대 목표(8)를 초과한 9로 생성하면") {
            Then("BingoInputException 이 발생한다") {
                shouldThrow<BingoInputException> {
                    BingoGoal.create(bingoSize.getMaxGoal() + 1, bingoSize)
                }
            }
        }
    }

    Given("isGoal 판정") {
        val bingoSize = BingoSize.cache(3)
        val bingoGoal = BingoGoal.create(3, bingoSize)

        When("빙고 개수가 목표(3)와 같으면") {
            Then("true 를 반환한다") {
                bingoGoal.isGoal(3) shouldBe true
            }
        }

        When("빙고 개수가 목표보다 작으면") {
            Then("false 를 반환한다") {
                bingoGoal.isGoal(2) shouldBe false
            }
        }

        When("빙고 개수가 목표보다 크면") {
            Then("false 를 반환한다") {
                bingoGoal.isGoal(4) shouldBe false
            }
        }
    }
})
