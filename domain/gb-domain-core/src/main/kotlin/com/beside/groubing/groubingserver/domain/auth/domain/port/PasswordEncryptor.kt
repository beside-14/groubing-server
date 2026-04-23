package com.beside.groubing.groubingserver.domain.auth.domain.port

interface PasswordEncryptor {
    fun encode(rawPassword: String): String

    fun matches(rawPassword: String, encodedPassword: String): Boolean
}
