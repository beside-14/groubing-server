package com.beside.groubing.groubingserver.global.config

import com.beside.groubing.groubingserver.global.filter.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig {
    private val AUTH_WHITELIST = arrayOf("/api/members", "/api/members/login")

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder()

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .csrf()
            .disable()
            .authorizeHttpRequests()
            .requestMatchers(HttpMethod.POST, *AUTH_WHITELIST)
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
}
