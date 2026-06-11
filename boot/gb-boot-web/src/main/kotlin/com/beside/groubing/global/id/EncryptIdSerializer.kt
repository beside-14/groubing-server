package com.beside.groubing.global.id

import com.beside.groubing.domain.common.id.ObfuscationType
import com.beside.groubing.domain.common.id.port.IdObfuscator
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider

class EncryptIdSerializer(
    private val idObfuscator: IdObfuscator,
    private val type: ObfuscationType
) : JsonSerializer<Long>() {

    override fun serialize(value: Long, gen: JsonGenerator, serializers: SerializerProvider) {
        gen.writeString(idObfuscator.encode(type, value))
    }
}
