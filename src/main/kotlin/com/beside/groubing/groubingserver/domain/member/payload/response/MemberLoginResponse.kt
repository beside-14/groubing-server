package com.beside.groubing.groubingserver.domain.member.payload.response

data class MemberLoginResponse(
    val id: Long,
    val email: String,
    val token: String
)
