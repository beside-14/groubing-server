package com.beside.groubing.global.id

import com.beside.groubing.domain.common.id.ObfuscationType
import com.beside.groubing.domain.common.id.exception.InvalidObfuscatedIdException
import com.beside.groubing.domain.common.id.port.IdObfuscator
import com.fasterxml.jackson.core.JsonParser
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk

class EncryptIdDeserializerTest : BehaviorSpec({

    val idObfuscator = mockk<IdObfuscator>()
    val deserializer = EncryptIdDeserializer(idObfuscator, ObfuscationType.MEMBER)

    Given("유효한 인코딩 문자열을 담은 JsonParser") {
        val encoded = "kRnB9P3LxYz1"
        val decoded = 42L
        every { idObfuscator.decode(ObfuscationType.MEMBER, encoded) } returns decoded

        val parser = mockk<JsonParser>()
        every { parser.valueAsString } returns encoded

        When("deserialize 를 호출하면") {
            val result = deserializer.deserialize(parser, mockk())

            Then("decode 결과 Long 값을 반환한다") {
                result shouldBe decoded
            }
        }
    }

    Given("잘못된 인코딩 문자열이 주어졌을 때") {
        val invalidEncoded = "!@#invalid"
        every {
            idObfuscator.decode(ObfuscationType.MEMBER, invalidEncoded)
        } throws InvalidObfuscatedIdException(invalidEncoded)

        val parser = mockk<JsonParser>()
        every { parser.valueAsString } returns invalidEncoded

        When("deserialize 를 호출하면") {
            Then("InvalidObfuscatedIdException 이 발생한다") {
                shouldThrow<InvalidObfuscatedIdException> {
                    deserializer.deserialize(parser, mockk())
                }
            }
        }
    }
})
