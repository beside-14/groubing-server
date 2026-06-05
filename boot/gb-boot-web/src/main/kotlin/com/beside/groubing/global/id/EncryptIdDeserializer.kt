package com.beside.groubing.global.id

import com.beside.groubing.global.domain.id.ObfuscationType
import com.beside.groubing.global.domain.id.port.IdObfuscator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer

class EncryptIdDeserializer(
    private val idObfuscator: IdObfuscator,
    private val type: ObfuscationType
) : JsonDeserializer<Long>() {

    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Long {
        return idObfuscator.decode(type, p.valueAsString)
    }
}
