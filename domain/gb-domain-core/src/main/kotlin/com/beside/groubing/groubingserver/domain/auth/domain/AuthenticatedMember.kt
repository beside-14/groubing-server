package com.beside.groubing.groubingserver.domain.auth.domain

import com.beside.groubing.groubingserver.domain.member.domain.Member

data class AuthenticatedMember(
    val member: Member,
    val accessToken: String
)
