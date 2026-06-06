package com.beside.groubing.global.id

import com.beside.groubing.domain.common.id.ObfuscationType
import com.beside.groubing.domain.common.id.exception.InvalidObfuscatedIdException
import com.beside.groubing.domain.common.id.port.IdObfuscator
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.core.convert.TypeDescriptor

class DecryptIdConverterTest : BehaviorSpec({

    val idObfuscator = mockk<IdObfuscator>()
    val converter = DecryptIdConverter(idObfuscator)

    Given("@DecryptId 어노테이션이 있는 target 타입") {
        val sourceType = mockk<TypeDescriptor>()
        val targetType = mockk<TypeDescriptor>()
        every { targetType.hasAnnotation(DecryptId::class.java) } returns true

        When("matches 를 호출하면") {
            val result = converter.matches(sourceType, targetType)

            Then("true 를 반환한다") {
                result shouldBe true
            }
        }
    }

    Given("@DecryptId 어노테이션이 없는 target 타입") {
        val sourceType = mockk<TypeDescriptor>()
        val targetType = mockk<TypeDescriptor>()
        every { targetType.hasAnnotation(DecryptId::class.java) } returns false

        When("matches 를 호출하면") {
            val result = converter.matches(sourceType, targetType)

            Then("false 를 반환한다 (일반 String→Long 변환과 충돌하지 않는다)") {
                result shouldBe false
            }
        }
    }

    Given("유효한 인코딩 문자열과 @DecryptId(MEMBER) 어노테이션") {
        val encoded = "kRnB9P3LxYz1"
        val decoded = 42L
        val annotation = mockk<DecryptId>()
        every { annotation.value } returns ObfuscationType.MEMBER
        every { idObfuscator.decode(ObfuscationType.MEMBER, encoded) } returns decoded

        val sourceType = mockk<TypeDescriptor>()
        val targetType = mockk<TypeDescriptor>()
        every { targetType.getAnnotation(DecryptId::class.java) } returns annotation

        When("convert 를 호출하면") {
            val result = converter.convert(encoded, sourceType, targetType)

            Then("디코딩된 Long 값을 반환한다") {
                result shouldBe decoded
            }
        }
    }

    Given("source 가 null") {
        val sourceType = mockk<TypeDescriptor>()
        val targetType = mockk<TypeDescriptor>()

        When("convert 를 호출하면") {
            val result = converter.convert(null, sourceType, targetType)

            Then("null 을 반환한다") {
                result shouldBe null
            }
        }
    }

    Given("잘못된 인코딩 문자열과 @DecryptId 어노테이션") {
        val invalidEncoded = "!@#invalid"
        val annotation = mockk<DecryptId>()
        every { annotation.value } returns ObfuscationType.MEMBER
        every {
            idObfuscator.decode(ObfuscationType.MEMBER, invalidEncoded)
        } throws InvalidObfuscatedIdException(invalidEncoded)

        val sourceType = mockk<TypeDescriptor>()
        val targetType = mockk<TypeDescriptor>()
        every { targetType.getAnnotation(DecryptId::class.java) } returns annotation

        When("convert 를 호출하면") {
            Then("InvalidObfuscatedIdException 이 발생한다") {
                shouldThrow<InvalidObfuscatedIdException> {
                    converter.convert(invalidEncoded, sourceType, targetType)
                }
            }
        }
    }
})
