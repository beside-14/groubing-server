package com.beside.groubing.groubingserver.extension

import com.beside.groubing.groubingserver.domain.member.domain.MemberRole
import com.beside.groubing.groubingserver.global.domain.security.JwtProvider

fun getHttpHeaderJwt(memberId: Long): String = "Bearer " + getJwt(memberId)

fun getJwt(memberId: Long): String = JwtProvider.createToken(memberId, MemberRole.MEMBER)
