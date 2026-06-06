package com.beside.groubing.global.id

import com.beside.groubing.domain.common.id.ObfuscationType

/** `@PathVariable` / `@RequestParam` 용. 요청 본문 필드는 [EncryptId] 가 양방향을 담당한다. */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class DecryptId(val value: ObfuscationType)
