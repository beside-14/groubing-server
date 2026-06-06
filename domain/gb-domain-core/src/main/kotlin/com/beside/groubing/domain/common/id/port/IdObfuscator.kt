package com.beside.groubing.domain.common.id.port

import com.beside.groubing.domain.common.id.ObfuscationType

interface IdObfuscator {
    fun encode(type: ObfuscationType, id: Long): String

    fun decode(type: ObfuscationType, encoded: String): Long
}
