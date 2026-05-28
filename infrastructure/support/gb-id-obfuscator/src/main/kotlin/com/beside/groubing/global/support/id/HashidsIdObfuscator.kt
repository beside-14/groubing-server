package com.beside.groubing.global.support.id

import com.beside.groubing.global.domain.id.ObfuscationType
import com.beside.groubing.global.domain.id.exception.InvalidObfuscatedIdException
import com.beside.groubing.global.domain.id.port.IdObfuscator
import org.hashids.Hashids
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

/**
 * Hashids 기반 [IdObfuscator] 구현.
 *
 * - `baseSalt` 와 [ObfuscationType.saltSuffix] 를 조합해 type 별로 서로 다른 인코딩 시드를 사용한다.
 * - [Hashids] 인스턴스는 type 별로 lazy 생성 + 캐시(스레드 안전).
 * - 디코딩 실패(빈 결과)는 [InvalidObfuscatedIdException] 으로 변환한다 — 잘못된 문자열·type 불일치 모두 동일한 입력 오류로 다룬다.
 */
@Component
class HashidsIdObfuscator(
    @Value("\${id-obfuscator.salt}") private val baseSalt: String,
    @Value("\${id-obfuscator.min-length:12}") private val minLength: Int
) : IdObfuscator {

    private val cache = ConcurrentHashMap<ObfuscationType, Hashids>()

    override fun encode(type: ObfuscationType, id: Long): String {
        return hashidsFor(type).encode(id)
    }

    override fun decode(type: ObfuscationType, encoded: String): Long {
        val decoded = hashidsFor(type).decode(encoded)
        if (decoded.isEmpty()) {
            throw InvalidObfuscatedIdException(encoded)
        }
        return decoded[0]
    }

    private fun hashidsFor(type: ObfuscationType): Hashids {
        return cache.computeIfAbsent(type) {
            Hashids("$baseSalt:${type.saltSuffix}", minLength)
        }
    }
}
