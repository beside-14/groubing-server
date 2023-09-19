package com.beside.groubing.groubingserver.domain.member.payload.command

import com.beside.groubing.groubingserver.domain.member.domain.SocialType

class SocialLoginCommand(
    val id: String,
    val email: String,
    val socialType: SocialType
)
