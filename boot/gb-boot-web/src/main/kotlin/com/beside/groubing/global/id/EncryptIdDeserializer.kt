package com.beside.groubing.global.id

import com.beside.groubing.global.domain.id.ObfuscationType
import com.beside.groubing.global.domain.id.port.IdObfuscator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer

/**
 * 요청 본문의 obfuscated id 문자열을 [Long] 으로 역직렬화한다.
 *
 * 디코딩 실패는 [com.beside.groubing.global.domain.id.exception.InvalidObfuscatedIdException] 으로
 * 위임된다(IdObfuscator 가 던짐).
 */
class EncryptIdDeserializer(
    private val idObfuscator: IdObfuscator,
    private val type: ObfuscationType
) : JsonDeserializer<Long>() {

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Long {
        return idObfuscator.decode(type, p.valueAsString)
    }
}
