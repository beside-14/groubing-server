package com.beside.groubing.groubingserver.global.interceptor

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mu.KLogging
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.util.ContentCachingResponseWrapper

@Component
class LoggingInterceptor(
    private val objectMapper: ObjectMapper
) : HandlerInterceptor {

    companion object : KLogging()

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        logger.info { "=====================================================================================" }
        logger.info {
            "Request  >>> ${request.method} ${request.requestURI} ${request.contentType} ${
                objectMapper.writeValueAsString(
                    request.parameterMap
                )
            }"
        }
        return true;
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        val responseWrapper = response as ContentCachingResponseWrapper
        val contentType = responseWrapper.contentType
        val status = HttpStatus.valueOf(responseWrapper.status)
        val responseBody = String(responseWrapper.contentAsByteArray)

        if (MediaType.APPLICATION_JSON_VALUE == contentType) {
            logger.info {
                "Response >>> ${request.method} ${request.requestURI} $contentType $status $responseBody"
            }
        } else {
            logger.info {
                "Response >>> ${request.method} ${request.requestURI} $contentType $status"
            }
        }
        logger.info { "=====================================================================================" }
    }
}
