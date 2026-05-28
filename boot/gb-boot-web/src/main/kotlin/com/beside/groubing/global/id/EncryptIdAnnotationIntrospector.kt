package com.beside.groubing.global.id

import com.beside.groubing.global.domain.id.port.IdObfuscator
import com.fasterxml.jackson.databind.introspect.Annotated
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector

/**
 * Jackson 이 직렬화·역직렬화 대상 필드의 [EncryptId] 어노테이션을 인식해
 * 그에 맞는 [EncryptIdSerializer] / [EncryptIdDeserializer] 를 사용하도록 연결한다.
 *
 * [ObfuscatedIdJacksonConfig] 에서 [com.fasterxml.jackson.databind.ObjectMapper] 에 등록한다.
 */
class EncryptIdAnnotationIntrospector(
    private val idObfuscator: IdObfuscator
) : NopAnnotationIntrospector() {

    override fun findSerializer(a: Annotated): Any? {
        val annotation = a.getAnnotation(EncryptId::class.java) ?: return null
        return EncryptIdSerializer(idObfuscator, annotation.value)
    }

    override fun findDeserializer(a: Annotated): Any? {
        val annotation = a.getAnnotation(EncryptId::class.java) ?: return null
        return EncryptIdDeserializer(idObfuscator, annotation.value)
    }
}
