package com.beside.groubing.global.support.id

import com.beside.groubing.domain.common.id.ObfuscationType
import com.beside.groubing.domain.common.id.exception.InvalidObfuscatedIdException
import com.beside.groubing.domain.common.id.port.IdObfuscator
import org.hashids.Hashids
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

/**
 * `baseSalt` + [ObfuscationType.saltSuffix] 로 type 별 시드를 격리한다.
 * 디코딩 실패(빈 결과)는 [InvalidObfuscatedIdException] 으로 통일 — 잘못된 문자열과 type 불일치를 같은 입력 오류로 다룬다.
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
