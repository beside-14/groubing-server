package com.beside.groubing.groubingserver.global.config

import java.util.Optional
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.domain.AuditorAware
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.security.core.context.SecurityContextHolder


@Configuration
@EnableJpaAuditing
class AuditingConfig {
    @Bean
    fun auditorProvider() = AuditorAware {
        Optional.of(SecurityContextHolder.getContext()
                .authentication
                .principal
                .toString()
        )
    }
}
