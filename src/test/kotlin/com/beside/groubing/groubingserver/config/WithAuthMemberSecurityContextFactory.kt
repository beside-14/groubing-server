package com.beside.groubing.groubingserver.config

import com.beside.groubing.groubingserver.global.domain.security.JwtProvider
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithSecurityContextFactory

class WithAuthMemberSecurityContextFactory : WithSecurityContextFactory<WithAuthMember> {
    override fun createSecurityContext(annotation: WithAuthMember): SecurityContext {
        val context = SecurityContextHolder.getContext()
        val jwt = JwtProvider.createToken(annotation.id, annotation.email, annotation.role)
        context.authentication = JwtProvider.getAuthentication(jwt)
        return context
    }
}
