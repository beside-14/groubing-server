package com.beside.groubing.global.id

import com.beside.groubing.global.domain.id.ObfuscationType
import com.beside.groubing.global.domain.id.port.IdObfuscator
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider

/**
 * [Long] id 를 [IdObfuscator.encode] 결과 문자열로 직렬화한다.
 *
 * 각 [EncryptId] 어노테이션마다 [EncryptIdAnnotationIntrospector] 가 type 을 캡처해
 * 별도 인스턴스를 만들어준다.
 */
class EncryptIdSerializer(
    private val idObfuscator: IdObfuscator,
    private val type: ObfuscationType
) : JsonSerializer<Long>() {

    override fun serialize(value: Long, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeString(idObfuscator.encode(type, value))
    }
}
