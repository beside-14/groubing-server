package com.beside.groubing.groubingserver.config

import com.beside.groubing.groubingserver.global.config.SecurityConfig
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.context.annotation.Import

@WithAuthMember
@Import(SecurityConfig::class)
@AutoConfigureRestDocs
@Target(AnnotationTarget.CLASS)
@Retention
annotation class ApiTest
