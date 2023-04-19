package com.beside.groubing.groubingserver.extension

import com.beside.groubing.groubingserver.domain.member.domain.MemberRole
import com.beside.groubing.groubingserver.global.domain.security.JwtProvider

fun getHttpHeaderJwt(): String = "Bearer " + getJwt()

fun getJwt(): String = JwtProvider.createToken(0L, "test@groubing.com", MemberRole.MEMBER)
