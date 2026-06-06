package com.beside.groubing.global.id

import com.beside.groubing.domain.common.id.ObfuscationType
import com.beside.groubing.domain.common.id.port.IdObfuscator
import com.fasterxml.jackson.core.JsonGenerator
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class EncryptIdSerializerTest : BehaviorSpec({

    val idObfuscator = mockk<IdObfuscator>()
    val serializer = EncryptIdSerializer(idObfuscator, ObfuscationType.MEMBER)

    Given("Long id 와 JsonGenerator 가 주어졌을 때") {
        val id = 42L
        val encoded = "kRnB9P3LxYz1"
        every { idObfuscator.encode(ObfuscationType.MEMBER, id) } returns encoded

        val gen = mockk<JsonGenerator>()
        every { gen.writeString(any<String>()) } returns Unit

        When("serialize 를 호출하면") {
            serializer.serialize(id, gen, mockk())

            Then("encode 결과 문자열을 JSON 으로 기록한다") {
                verify { gen.writeString(encoded) }
            }
        }
    }
})
