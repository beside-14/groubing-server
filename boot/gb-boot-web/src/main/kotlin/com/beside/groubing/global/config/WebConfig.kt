package com.beside.groubing.global.config

import com.beside.groubing.domain.common.id.port.IdObfuscator
import com.beside.groubing.global.id.DecryptIdConverter
import com.beside.groubing.global.interceptor.LoggingInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.format.FormatterRegistry
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val loggingInterceptor: LoggingInterceptor,
    private val idObfuscator: IdObfuscator
) : WebMvcConfigurer {
    override fun addArgumentResolvers(argumentResolvers: MutableList<HandlerMethodArgumentResolver>) {
        val resolver = PageableHandlerMethodArgumentResolver()
        resolver.setOneIndexedParameters(true)
        argumentResolvers.add(resolver)
    }

    override fun addFormatters(registry: FormatterRegistry) {
        registry.addConverter(DecryptIdConverter(idObfuscator))
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(loggingInterceptor)
            .addPathPatterns("/api/**")
    }
}
