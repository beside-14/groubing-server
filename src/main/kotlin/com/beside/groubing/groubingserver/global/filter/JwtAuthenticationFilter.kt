package com.beside.groubing.groubingserver.global.filter

import com.beside.groubing.groubingserver.global.domain.security.JwtProvider
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthenticationFilter : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")
        if (authHeader.isNullOrBlank() || !authHeader.startsWith("Bearer ")) {
            return filterChain.doFilter(request, response)
        }
        validateJwt(authHeader.substring("Bearer ".length), filterChain, request, response)
    }

    private fun validateJwt(
        jwt: String,
        filterChain: FilterChain,
        request: HttpServletRequest,
        response: HttpServletResponse
    ) {
        if (JwtProvider.isValidToken(jwt)) {
            SecurityContextHolder.getContext().authentication = JwtProvider.getAuthentication(jwt)
        }
        filterChain.doFilter(request, response)
    }
}
