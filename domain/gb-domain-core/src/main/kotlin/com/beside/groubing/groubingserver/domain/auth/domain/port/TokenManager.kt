package com.beside.groubing.groubingserver.domain.auth.domain.port

interface TokenManager {
    fun generateAccessToken(memberId: Long, role: String): String
}
