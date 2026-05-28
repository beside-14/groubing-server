package com.beside.groubing.global.id

import com.beside.groubing.global.domain.id.port.IdObfuscator
import com.beside.groubing.global.support.id.HashidsIdObfuscator
import com.fasterxml.jackson.databind.AnnotationIntrospector
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary

/**
 * `@ApiTest` 슬라이스에서 [IdObfuscator] 빈을 공급한다.
 *
 * - `@WebMvcTest` 는 일반 `@Component`(다른 모듈의 [HashidsIdObfuscator]) 를 스캔하지 않으므로
 *   [com.beside.groubing.global.config.WebConfig] 의 의존성 주입이 실패한다 → 테스트 전용 빈으로 대체.
 * - 직렬화/역직렬화 어노테이션 인식을 위해 [EncryptIdAnnotationIntrospector] 도 동일한 방식으로 등록한다.
 *
 * 기본 [ApiTest] 어노테이션이 `@Import` 로 함께 가져온다.
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
