package com.beside.groubing.groubingserver.extension

import org.springframework.http.HttpMethod
import org.springframework.restdocs.generate.RestDocumentationGenerator
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders

fun multipart(
    httpMethod: HttpMethod,
    urlTemplate: String,
    vararg urlVariables: Any
): MockMultipartHttpServletRequestBuilder {
    return MockMvcRequestBuilders.multipart(httpMethod, urlTemplate, *urlVariables)
        .requestAttr(
            RestDocumentationGenerator.ATTRIBUTE_NAME_URL_TEMPLATE,
            urlTemplate
        ) as MockMultipartHttpServletRequestBuilder
}
