package com.beside.groubing.domain.auth.domain

import com.beside.groubing.domain.member.domain.Member

data class AuthenticatedMember(
    val member: Member,
    val accessToken: String
)
