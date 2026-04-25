package com.beside.groubing.config

import com.beside.groubing.domain.member.domain.MemberRole
import org.springframework.security.test.context.support.WithSecurityContext

@Target(AnnotationTarget.CLASS)
@Retention
@WithSecurityContext(factory = WithAuthMemberSecurityContextFactory::class)
annotation class WithAuthMember(
    val id: Long = 1L,
    val email: String = "test@groubing.com",
    val role: MemberRole = MemberRole.MEMBER
)
