package com.beside.groubing.domain.auth.domain.port

interface TokenManager {
    fun generateAccessToken(memberId: Long, role: String): String
}
