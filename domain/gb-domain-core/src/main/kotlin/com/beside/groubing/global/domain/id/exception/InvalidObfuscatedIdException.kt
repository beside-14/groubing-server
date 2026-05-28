package com.beside.groubing.global.domain.id.exception

/**
 * obfuscated id 문자열을 디코딩할 수 없을 때 발생한다.
 *
 * 클라이언트가 잘못된/위조된 id 문자열을 전달하거나, 다른 [com.beside.groubing.global.domain.id.ObfuscationType]
 * 으로 인코딩된 문자열을 잘못된 type 으로 디코딩하려 할 때 던져진다.
 */
class InvalidObfuscatedIdException(encoded: String) :
    RuntimeException("유효하지 않은 식별자입니다. : $encoded")
