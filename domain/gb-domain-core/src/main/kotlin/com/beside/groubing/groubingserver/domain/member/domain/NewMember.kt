package com.beside.groubing.groubingserver.domain.member.domain

data class NewMember(
    val email: String?,
    val password: String,
    val nickname: String,
    val role: MemberRole,
    val memberType: MemberType
)
