package com.beside.groubing.global.id

import com.beside.groubing.global.domain.id.ObfuscationType

/**
 * 응답·요청 본문의 `Long` id 필드를 obfuscated 문자열로 직렬화·역직렬화하도록 표시한다.
 *
 * - 응답 DTO 필드에 붙이면 [com.fasterxml.jackson.databind.ObjectMapper] 직렬화 시
 *   [com.beside.groubing.global.domain.id.port.IdObfuscator.encode] 결과(문자열)로 변환된다.
 * - 요청 DTO 필드에 붙이면 역직렬화 시 클라이언트가 보낸 문자열을 [Long] 으로 디코딩한다.
 *
 * Path / query parameter 에는 [DecryptId] 를 사용한다.
 */
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class EncryptId(val value: ObfuscationType)
