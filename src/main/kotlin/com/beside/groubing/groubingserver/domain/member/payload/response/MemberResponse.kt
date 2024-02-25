package com.beside.groubing.groubingserver.domain.member.payload.response

data class MemberResponse(
    val id: Long,
    val email: String,
    val nickname: String,
    val profileUrl: String?,
    val token: String,
    val notificationReceive: Boolean
)
