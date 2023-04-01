package com.beside.groubing.groubingserver.docs

import org.springframework.restdocs.snippet.Attributes.Attribute
import org.springframework.restdocs.snippet.Attributes.key

class RestDocsUtils {

    companion object {

        const val DEFAULT_KEY = "default"
        const val FORMAT_KEY = "format"
        const val EXAMPLE_KEY = "example"
        const val DATE_FORMAT = "yyyy-MM-dd"
        const val DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss"
        fun emptyDefaultValue(): Attribute = key(DEFAULT_KEY).value("")
        fun emptyFormat(): Attribute = key(FORMAT_KEY).value("")
        fun emptyExample(): Attribute = key(EXAMPLE_KEY).value("")

        fun defaultValue(value: String): Attribute = key(DEFAULT_KEY).value(value)
        fun customFormat(value: String): Attribute = key(FORMAT_KEY).value(value)
        fun customSample(value: String): Attribute = key(EXAMPLE_KEY).value(value)
    }
}
