package com.beside.groubing.config

import com.beside.groubing.global.config.SecurityConfig
import com.beside.groubing.global.id.TestIdObfuscatorConfig
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.context.annotation.Import

@WithAuthMember
@Import(SecurityConfig::class, TestIdObfuscatorConfig::class)
@AutoConfigureRestDocs
@Target(AnnotationTarget.CLASS)
@Retention
annotation class ApiTest
