package com.beside.groubing.groubingserver.global.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.web.PageableHandlerMethodArgumentResolver
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig : WebMvcConfigurer {
    override fun addArgumentResolvers(argumentResolvers: MutableList<HandlerMethodArgumentResolver>) {
        val resolver = PageableHandlerMethodArgumentResolver()
        resolver.setOneIndexedParameters(true)
        argumentResolvers.add(resolver)
    }
}
