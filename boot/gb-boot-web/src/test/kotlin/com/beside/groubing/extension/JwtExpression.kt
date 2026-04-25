package com.beside.groubing.extension

import com.beside.groubing.domain.member.domain.MemberRole
import com.beside.groubing.global.domain.security.JwtProvider

fun getHttpHeaderJwt(memberId: Long): String = "Bearer " + getJwt(memberId)

fun getJwt(memberId: Long): String = JwtProvider.createToken(memberId, MemberRole.MEMBER.name)
