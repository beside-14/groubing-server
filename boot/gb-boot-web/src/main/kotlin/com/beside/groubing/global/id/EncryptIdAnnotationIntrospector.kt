package com.beside.groubing.global.id

import com.beside.groubing.global.domain.id.port.IdObfuscator
import com.fasterxml.jackson.databind.introspect.Annotated
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector

/**
 * Jackson 이 직렬화·역직렬화 대상 필드의 [EncryptId] 어노테이션을 인식해
 * 그에 맞는 [EncryptIdSerializer] / [EncryptIdDeserializer] 를 사용하도록 연결한다.
 *
 * - 단일 `Long` 필드는 [findSerializer] / [findDeserializer] 로 처리.
 * - `List<Long>` / `Collection<Long>` 등 컨테이너 필드는 [findContentSerializer] / [findContentDeserializer]
 *   로 처리해 각 요소(Long)에 같은 obfuscation 을 적용한다. Jackson 은 컨테이너 자체에 대해서는
 *   기본 직렬화기를 쓰고, 요소 처리에만 우리가 반환한 (de)serializer 를 적용한다.
 *
 * [ObfuscatedIdJacksonConfig] 에서 [com.fasterxml.jackson.databind.ObjectMapper] 에 등록한다.
 */
class EncryptIdAnnotationIntrospector(
    private val idObfuscator: IdObfuscator
) : NopAnnotationIntrospector() {

    override fun findSerializer(a: Annotated): Any? {
        val annotation = a.getAnnotation(EncryptId::class.java) ?: return null
        // 컨테이너 필드(List<Long> 등)는 findContentSerializer 가 맡고, 여기서는 단일 Long 만 처리한다.
        if (a.isContainerField()) return null
        return EncryptIdSerializer(idObfuscator, annotation.value)
    }

    override fun findDeserializer(a: Annotated): Any? {
        val annotation = a.getAnnotation(EncryptId::class.java) ?: return null
        if (a.isContainerField()) return null
        return EncryptIdDeserializer(idObfuscator, annotation.value)
    }

    override fun findContentSerializer(a: Annotated): Any? {
        val annotation = a.getAnnotation(EncryptId::class.java) ?: return null
        return EncryptIdSerializer(idObfuscator, annotation.value)
    }

    override fun findContentDeserializer(a: Annotated): Any? {
        val annotation = a.getAnnotation(EncryptId::class.java) ?: return null
        return EncryptIdDeserializer(idObfuscator, annotation.value)
    }

    private fun Annotated.isContainerField(): Boolean =
        Collection::class.java.isAssignableFrom(rawType) ||
            Map::class.java.isAssignableFrom(rawType) ||
            rawType.isArray
}
