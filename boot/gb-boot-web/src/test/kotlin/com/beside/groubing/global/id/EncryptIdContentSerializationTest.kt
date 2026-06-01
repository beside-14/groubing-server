package com.beside.groubing.global.id

import com.beside.groubing.global.domain.id.ObfuscationType
import com.beside.groubing.global.domain.id.port.IdObfuscator
import com.beside.groubing.global.support.id.HashidsIdObfuscator
import com.fasterxml.jackson.databind.AnnotationIntrospector
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainExactly
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
}) {
    /**
     * 테스트 전용 DTO — `List<Long>` 필드 + `@field:EncryptId` 부착.
     * 실제 응답·요청 DTO 와 동일한 어노테이션 패턴.
     */
    data class ContainerDto(
        @field:EncryptId(ObfuscationType.MEMBER)
        val memberIds: List<Long>
    )
}
