package com.beside.groubing.extension

import com.beside.groubing.global.domain.id.ObfuscationType
import com.beside.groubing.global.support.id.HashidsIdObfuscator

/** `TestIdObfuscatorConfig` 와 동일 salt — MockMvc URL 에 넣을 obfuscated 문자열 생성. */
private val testIdObfuscator = HashidsIdObfuscator(
    baseSalt = "test-salt",
    minLength = 12
)

fun Long.encodedAs(type: ObfuscationType): String = testIdObfuscator.encode(type, this)
