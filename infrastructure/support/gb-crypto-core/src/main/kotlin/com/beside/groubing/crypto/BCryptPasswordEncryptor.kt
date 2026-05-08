package com.beside.groubing.crypto

import com.beside.groubing.domain.auth.domain.port.PasswordEncryptor
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class BCryptPasswordEncryptor : PasswordEncryptor {
    private val passwordEncoder = BCryptPasswordEncoder()

    override fun encode(rawPassword: String): String {
        return passwordEncoder.encode(rawPassword)
    }

    override fun matches(rawPassword: String, encodedPassword: String): Boolean {
        return passwordEncoder.matches(rawPassword, encodedPassword)
    }
}
