package com.beside.groubing.global.id

import com.beside.groubing.global.domain.id.port.IdObfuscator
import com.fasterxml.jackson.databind.AnnotationIntrospector
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration

/** 기존 introspector(Kotlin 모듈 등)는 보존하기 위해 [AnnotationIntrospector.pair] 로 합성한다. */
@Configuration
class ObfuscatedIdJacksonConfig(
    private val objectMapper: ObjectMapper,
    private val idObfuscator: IdObfuscator
) {
    @PostConstruct
    fun registerEncryptIdIntrospector() {
        val introspector = EncryptIdAnnotationIntrospector(idObfuscator)
        val existing = objectMapper.serializationConfig.annotationIntrospector
        objectMapper.setAnnotationIntrospector(
            AnnotationIntrospector.pair(introspector, existing)
        )
    }
}
