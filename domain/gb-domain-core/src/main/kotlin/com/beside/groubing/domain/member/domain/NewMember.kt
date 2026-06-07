package com.beside.groubing.domain.member.domain

data class NewMember(
    val loginId: String?,
    val password: String,
    val nickname: String,
    val role: MemberRole,
    val memberType: MemberType
)
