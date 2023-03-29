package com.beside.groubing.groubingserver.global.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import java.util.*

@Configuration
@EnableJpaAuditing
class AuditingConfig {
    @Bean
    fun auditorProvider(): AuditorAware<String>? {
        //        Spring Security 적용 후 주석 된 코드로 대체
        //        return () -> Optional.of(SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString());
        return AuditorAware { Optional.of("ADMIN") }
    }
}