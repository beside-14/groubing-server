package com.beside.groubing.global.id

import com.beside.groubing.global.domain.id.ObfuscationType

/** 응답·요청 본문의 `Long` id 필드용. path / query parameter 는 [DecryptId] 를 쓴다. */
@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class EncryptId(val value: ObfuscationType)
