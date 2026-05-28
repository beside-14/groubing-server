package com.beside.groubing.global.id

import com.beside.groubing.global.domain.id.ObfuscationType

/**
 * `@PathVariable` / `@RequestParam` 으로 들어오는 obfuscated id 문자열을 [Long] 으로 복호화하도록 표시한다.
 *
 * 요청 본문 필드의 양방향 변환에는 [EncryptId] 를 사용한다 (역직렬화도 같은 어노테이션이 담당).
 *
 * 동작은 [com.beside.groubing.global.id.DecryptIdConverter] 가 [org.springframework.format.FormatterRegistry]
 * 에 등록되어 처리한다.
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class DecryptId(val value: ObfuscationType)
