package com.beside.groubing.global.domain.security

import com.beside.groubing.domain.auth.domain.port.TokenManager
import org.springframework.stereotype.Component

@Component
class JwtTokenManager : TokenManager {
    override fun generateAccessToken(memberId: Long, role: String): String {
        return JwtProvider.createToken(memberId, role)
    }
}
