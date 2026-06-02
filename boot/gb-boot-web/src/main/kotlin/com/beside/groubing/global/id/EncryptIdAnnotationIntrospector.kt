package com.beside.groubing.global.id

import com.beside.groubing.global.domain.id.port.IdObfuscator
import com.fasterxml.jackson.databind.introspect.Annotated
import com.fasterxml.jackson.databind.introspect.NopAnnotationIntrospector

class EncryptIdAnnotationIntrospector(
    private val idObfuscator: IdObfuscator
) : NopAnnotationIntrospector() {

    override fun findSerializer(a: Annotated): Any? {
        val annotation = a.getAnnotation(EncryptId::class.java) ?: return null
        if (a.isContainerField()) return null  // 컨테이너는 findContentSerializer 가 맡는다
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
