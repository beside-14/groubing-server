package com.beside.groubing.global.domain.id.port

import com.beside.groubing.global.domain.id.ObfuscationType

/**
 * 숫자 id 를 외부 노출용 문자열로 인코딩 / 디코딩하는 포트.
 *
 * 구현체는 인프라 모듈(`gb-id-obfuscator`)에서 제공한다. type 별로 salt 가 격리되므로
 * 동일한 인코딩 문자열을 다른 type 으로 디코딩하면 다른 값이 나오거나 실패한다.
 */
interface IdObfuscator {
    fun encode(type: ObfuscationType, id: Long): String

    fun decode(type: ObfuscationType, encoded: String): Long
}
