package com.beside.groubing.groubingserver.global.config

import com.beside.groubing.groubingserver.global.filter.JwtAuthenticationFilter
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig {
    companion object {
        private val POST_AUTH_WHITELIST = arrayOf("/api/members", "/api/members/login", "/api/members/find-email")
        private val PATCH_AUTH_WHITELIST = arrayOf("/api/members/*/password")
        private val STATIC_RESOURCES = arrayOf("/docs/**", "/*/*.png", "/***.jpg", "/*/*.jpeg")
    }

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf()
            .disable()
            .authorizeHttpRequests()
            .requestMatchers(HttpMethod.POST, *POST_AUTH_WHITELIST)
            .permitAll()
            .requestMatchers(HttpMethod.PATCH, *PATCH_AUTH_WHITELIST)
            .permitAll()
            .anyRequest()
            .authenticated()
            .and()
            // 세션 상태 비저장 설정
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .addFilterBefore(
                JwtAuthenticationFilter(),
                UsernamePasswordAuthenticationFilter::class.java
            ).build()
    }

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer? {
        return WebSecurityCustomizer { web: WebSecurity ->
            web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .requestMatchers(*STATIC_RESOURCES)
        }
    }
}
