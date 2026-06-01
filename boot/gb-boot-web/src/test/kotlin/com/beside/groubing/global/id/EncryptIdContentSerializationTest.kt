package com.beside.groubing.global.id

import com.beside.groubing.global.domain.id.ObfuscationType
import com.beside.groubing.global.domain.id.exception.InvalidObfuscatedIdException
import com.beside.groubing.global.domain.id.port.IdObfuscator
import com.beside.groubing.global.support.id.HashidsIdObfuscator
import com.fasterxml.jackson.databind.AnnotationIntrospector
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain

/**
 * `@EncryptId` 가 컨테이너(List<Long>) 필드에 부착됐을 때
 * [EncryptIdAnnotationIntrospector.findContentSerializer] / `findContentDeserializer`
 * 가 각 요소(Long)를 obfuscate 하는지 검증한다.
 *
 * 다른 단위 테스트들이 mock 기반인 반면, 이 테스트는 실제 [ObjectMapper] 에 introspector 를 등록해
 * 직렬화 / 역직렬화 round-trip 까지 확인한다.
 */
class EncryptIdContentSerializationTest : BehaviorSpec({

    val idObfuscator: IdObfuscator = HashidsIdObfuscator(baseSalt = "test-salt", minLength = 12)

    val mapper: ObjectMapper = jacksonObjectMapper().apply {
        val introspector = EncryptIdAnnotationIntrospector(idObfuscator)
        val existing = serializationConfig.annotationIntrospector
        setAnnotationIntrospector(AnnotationIntrospector.pair(introspector, existing))
    }

    Given("List<Long> 필드에 @EncryptId(MEMBER) 가 부착된 DTO") {
        val original = ContainerDto(memberIds = listOf(1L, 2L, 3L))
        val expectedEncoded = original.memberIds.map { idObfuscator.encode(ObfuscationType.MEMBER, it) }

        When("직렬화하면") {
            val json = mapper.writeValueAsString(original)

            Then("각 요소가 인코딩된 문자열 배열로 출력된다") {
                expectedEncoded.forEach { json shouldContain "\"$it\"" }
                // 숫자 그대로 나오면 안 됨
                json shouldContain "\"memberIds\":["
            }

            Then("역직렬화하면 원본 Long 리스트로 복원된다") {
                val restored = mapper.readValue(json, ContainerDto::class.java)
                restored.memberIds shouldContainExactly original.memberIds
            }
        }
    }

    Given("빈 리스트") {
        val original = ContainerDto(memberIds = emptyList())

        When("직렬화·역직렬화 round-trip 하면") {
            val json = mapper.writeValueAsString(original)
            val restored = mapper.readValue(json, ContainerDto::class.java)

            Then("빈 리스트가 그대로 유지된다") {
                restored.memberIds shouldBe emptyList()
            }
        }
    }

    Given("List 의 일부 요소가 잘못된 인코딩 문자열") {
        val validEncoded = idObfuscator.encode(ObfuscationType.MEMBER, 1L)
        val invalidJson = """{"memberIds":["$validEncoded","!@#invalid"]}"""

        When("역직렬화하면") {
            Then("InvalidObfuscatedIdException 이 발생한다") {
                // Jackson 은 Kotlin data class 생성자 호출 단계에서 던진 예외를
                // ValueInstantiationException 으로 감싸므로 원인 체인을 따라 확인한다.
                val thrown = shouldThrow<JsonMappingException> {
                    mapper.readValue(invalidJson, ContainerDto::class.java)
                }
                // Jackson 이 ValueInstantiation/JsonMapping 으로 감싸 던지므로 원인 체인에서 확인.
                val hasInvalidIdInChain = generateSequence(thrown as Throwable?) { it.cause }
                    .any { it is InvalidObfuscatedIdException }
                hasInvalidIdInChain shouldBe true
            }
        }
    }

    Given("Set<Long> 필드에 @EncryptId(BINGO_BOARD) 가 부착된 DTO") {
        val original = SetContainerDto(bingoBoardIds = setOf(10L, 20L, 30L))

        When("직렬화·역직렬화 round-trip 하면") {
            val json = mapper.writeValueAsString(original)
            val restored = mapper.readValue(json, SetContainerDto::class.java)

            Then("각 요소가 BINGO_BOARD type 으로 인코딩되어 출력된다") {
                original.bingoBoardIds.forEach { id ->
                    val encoded = idObfuscator.encode(ObfuscationType.BINGO_BOARD, id)
                    json shouldContain "\"$encoded\""
                }
            }

            Then("Set 의 원본 요소들로 복원된다 (순서 무관)") {
                restored.bingoBoardIds shouldContainExactlyInAnyOrder original.bingoBoardIds
            }
        }
    }
}) {
    /**
     * 테스트 전용 DTO — `List<Long>` 필드 + `@field:EncryptId` 부착.
     * 실제 응답·요청 DTO 와 동일한 어노테이션 패턴.
     */
    data class ContainerDto(
        @field:EncryptId(ObfuscationType.MEMBER)
        val memberIds: List<Long>
    )

    /**
     * Set 컨테이너 검증용 DTO — List 외 Collection 구현에도 introspector 가 적용되는지 확인.
     */
    data class SetContainerDto(
        @field:EncryptId(ObfuscationType.BINGO_BOARD)
        val bingoBoardIds: Set<Long>
    )
}
