package com.beside.groubing.domain.auth.domain

import com.beside.groubing.domain.auth.domain.port.PasswordEncryptor
import com.beside.groubing.domain.member.exception.MemberInputException
import org.springframework.stereotype.Component

@Component
class PasswordVerifier(
    private val passwordEncryptor: PasswordEncryptor
) {
    fun verify(rawPassword: String, encodedPassword: String) {
        if (!passwordEncryptor.matches(rawPassword, encodedPassword)) {
            throw MemberInputException("비밀번호가 일치하지 않습니다.")
        }
    }
}
