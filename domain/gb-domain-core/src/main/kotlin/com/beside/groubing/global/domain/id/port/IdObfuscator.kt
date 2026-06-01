package com.beside.groubing.global.domain.id.port

import com.beside.groubing.global.domain.id.ObfuscationType

interface IdObfuscator {
    fun encode(type: ObfuscationType, id: Long): String

    fun decode(type: ObfuscationType, encoded: String): Long
}
