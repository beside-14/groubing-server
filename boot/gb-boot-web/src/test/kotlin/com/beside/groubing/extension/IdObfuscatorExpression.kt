package com.beside.groubing.extension

import com.beside.groubing.global.domain.id.ObfuscationType
import com.beside.groubing.global.support.id.HashidsIdObfuscator

/**
 * 테스트 전용 ID 인코딩 헬퍼.
 *
 * `TestIdObfuscatorConfig` 가 빈으로 등록한 [HashidsIdObfuscator] 와 동일한 salt 를 써서
 * MockMvc 요청 URL 에 넣을 obfuscated 문자열을 만든다. 사용 예:
 *
 * ```kotlin
 * val id = 1L
 * patch("/api/members/{id}/nickname", id.encodedAs(ObfuscationType.MEMBER))
 * ```
 */
private val testIdObfuscator = HashidsIdObfuscator(
    baseSalt = "test-salt",
    minLength = 12
)

fun Long.encodedAs(type: ObfuscationType): String = testIdObfuscator.encode(type, this)
