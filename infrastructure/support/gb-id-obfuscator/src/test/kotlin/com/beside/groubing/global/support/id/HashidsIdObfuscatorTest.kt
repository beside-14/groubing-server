package com.beside.groubing.global.support.id

import com.beside.groubing.domain.common.id.ObfuscationType
import com.beside.groubing.domain.common.id.exception.InvalidObfuscatedIdException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldHaveMinLength

class HashidsIdObfuscatorTest : BehaviorSpec({

    val obfuscator = HashidsIdObfuscator(
        baseSalt = "test-salt",
        minLength = 12
    )

    Given("Long 타입 ID가 주어졌을 때") {
        val id = 42L

        When("인코딩하면") {
            val encoded = obfuscator.encode(ObfuscationType.MEMBER, id)

            Then("min-length 이상의 문자열이 반환된다") {
                encoded shouldHaveMinLength 12
            }

            Then("디코딩하면 원래 값으로 복원된다") {
                obfuscator.decode(ObfuscationType.MEMBER, encoded) shouldBe id
            }
        }
    }

    Given("서로 다른 ID가 주어졌을 때") {
        val id1 = 1L
        val id2 = 2L

        When("같은 type 으로 각각 인코딩하면") {
            val encoded1 = obfuscator.encode(ObfuscationType.MEMBER, id1)
            val encoded2 = obfuscator.encode(ObfuscationType.MEMBER, id2)

            Then("서로 다른 문자열이 생성된다") {
                encoded1 shouldNotBe encoded2
            }
        }
    }

    Given("같은 ID라도 ObfuscationType이 다르면") {
        val id = 1L

        When("MEMBER 와 BINGO_BOARD 로 각각 인코딩하면") {
            val memberEncoded = obfuscator.encode(ObfuscationType.MEMBER, id)
            val bingoEncoded = obfuscator.encode(ObfuscationType.BINGO_BOARD, id)

            Then("서로 다른 문자열이 생성된다") {
                memberEncoded shouldNotBe bingoEncoded
            }

            Then("각자의 type 으로 디코딩하면 원래 값으로 복원된다") {
                obfuscator.decode(ObfuscationType.MEMBER, memberEncoded) shouldBe id
                obfuscator.decode(ObfuscationType.BINGO_BOARD, bingoEncoded) shouldBe id
            }
        }
    }

    Given("잘못된 인코딩 문자열이 주어졌을 때") {
        val invalidEncoded = "!@#invalid"

        When("디코딩하면") {
            Then("InvalidObfuscatedIdException 이 발생한다") {
                shouldThrow<InvalidObfuscatedIdException> {
                    obfuscator.decode(ObfuscationType.MEMBER, invalidEncoded)
                }
            }
        }
    }
})
