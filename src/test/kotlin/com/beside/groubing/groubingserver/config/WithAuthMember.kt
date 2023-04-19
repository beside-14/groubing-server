package com.beside.groubing.groubingserver.config

import com.beside.groubing.groubingserver.domain.member.domain.MemberRole
import org.springframework.security.test.context.support.WithSecurityContext

@Target(AnnotationTarget.CLASS)
@Retention
@WithSecurityContext(factory = WithAuthMemberSecurityContextFactory::class)
annotation class WithAuthMember(
    val id: Long = 0L,
    val email: String = "test@groubing.com",
    val role: MemberRole = MemberRole.MEMBER
)
