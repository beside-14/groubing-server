package com.beside.groubing.groubingserver.global.domain.security

import com.beside.groubing.groubingserver.domain.auth.domain.port.TokenManager
import org.springframework.stereotype.Component

@Component
class JwtTokenManager : TokenManager {
    override fun generateAccessToken(memberId: Long, role: String): String {
        return JwtProvider.createToken(memberId, role)
    }
}
