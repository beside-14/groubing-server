package com.beside.groubing.domain.bingo.domain

import com.beside.groubing.domain.bingo.exception.BingoInputException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class BingoSizeTest : BehaviorSpec({

    Given("BingoSize.cache 로 빙고 사이즈를 생성할 때") {
        When("최소값 3으로 생성하면") {
            val bingoSize = BingoSize.cache(3)

            Then("size 가 3인 객체가 생성된다") {
                bingoSize.size shouldBe 3
            }
        }

        When("최대값 10으로 생성하면") {
            val bingoSize = BingoSize.cache(10)

            Then("size 가 10인 객체가 생성된다") {
                bingoSize.size shouldBe 10
            }
        }

        When("중간값 5로 생성하면") {
            val bingoSize = BingoSize.cache(5)

            Then("size 가 5인 객체가 생성된다") {
                bingoSize.size shouldBe 5
            }
        }

        When("같은 사이즈를 두 번 생성하면") {
            val first = BingoSize.cache(4)
            val second = BingoSize.cache(4)

            Then("캐시된 동일 인스턴스가 반환된다") {
                (first === second) shouldBe true
            }
        }

        When("최소값 미만인 2로 생성하면") {
            Then("BingoInputException 이 발생한다") {
                shouldThrow<BingoInputException> {
                    BingoSize.cache(2)
                }.message shouldBe "빙고 사이즈가 잘못 되었습니다."
            }
        }

        When("최대값 초과인 11로 생성하면") {
            Then("BingoInputException 이 발생한다") {
                shouldThrow<BingoInputException> {
                    BingoSize.cache(11)
                }.message shouldBe "빙고 사이즈가 잘못 되었습니다."
            }
        }

        When("0으로 생성하면") {
            Then("BingoInputException 이 발생한다") {
                shouldThrow<BingoInputException> {
                    BingoSize.cache(0)
                }
            }
        }

        When("음수로 생성하면") {
            Then("BingoInputException 이 발생한다") {
                shouldThrow<BingoInputException> {
                    BingoSize.cache(-1)
                }
            }
        }
    }

    Given("getMaxGoal 파생 속성") {
        When("size 가 3이면") {
            Then("최대 목표는 (3*2)+2 = 8 이다") {
                BingoSize.cache(3).getMaxGoal() shouldBe 8
            }
        }

        When("size 가 10이면") {
            Then("최대 목표는 (10*2)+2 = 22 이다") {
                BingoSize.cache(10).getMaxGoal() shouldBe 22
            }
        }
    }
})
