package com.beside.groubing.groubingserver.domain.member.payload.response

data class MemberDetailResponse(
    val id: Long,
    val email: String,
    val nickname: String,
    val profileUrl: String?
)
