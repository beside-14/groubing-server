package com.beside.groubing.global.id

import com.beside.groubing.domain.common.id.port.IdObfuscator
import com.beside.groubing.global.support.id.HashidsIdObfuscator
import com.fasterxml.jackson.databind.AnnotationIntrospector
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

/**
 * `@WebMvcTest` 슬라이스는 다른 모듈의 `@Component`([HashidsIdObfuscator]) 를 스캔하지 않아
 * [com.beside.groubing.global.config.WebConfig] 주입이 실패한다 — 그 자리를 메우는 테스트 전용 빈.
 * `@ApiTest` 가 `@Import` 로 함께 가져온다.
 */
@TestConfiguration
class TestIdObfuscatorConfig {

    @Bean
    @Primary
    fun testIdObfuscator(): IdObfuscator {
        return HashidsIdObfuscator(
            baseSalt = "test-salt",
            minLength = 12
        )
    }

    @Bean
    fun testEncryptIdIntrospectorInit(
        objectMapper: ObjectMapper,
        idObfuscator: IdObfuscator
    ): EncryptIdAnnotationIntrospector {
        val introspector = EncryptIdAnnotationIntrospector(idObfuscator)
        val existing = objectMapper.serializationConfig.annotationIntrospector
        objectMapper.setAnnotationIntrospector(
            AnnotationIntrospector.pair(introspector, existing)
        )
        return introspector
    }
}
