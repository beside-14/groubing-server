package com.beside.groubing.groubingserver.docs

import com.beside.groubing.groubingserver.domain.member.domain.MemberRole
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.restdocs.request.ParameterDescriptor
import org.springframework.restdocs.request.RequestDocumentation.pathParameters
import org.springframework.restdocs.snippet.Snippet
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.ResultActionsDsl
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print

fun requestBody(vararg fields: CustomDescriptor<FieldDescriptor>): Snippet {
    return requestFields(fields.map { it.descriptor as FieldDescriptor })
}

fun responseBody(vararg fields: CustomDescriptor<FieldDescriptor>): Snippet {
    val snippets =
        fields.toMutableList() + ("code" type STRING means "Http 응답 코드") + ("message" type STRING means "Http 응답 메세지")
    return responseFields(snippets.map { it.descriptor as FieldDescriptor })
}

fun pathVariables(vararg paths: CustomDescriptor<ParameterDescriptor>): Snippet =
    pathParameters(paths.map { it.descriptor as ParameterDescriptor })

fun ResultActionsDsl.andDocument(identifier: String, vararg snippets: Snippet) {
    andDo {
        print()
        handle(
            document(
                identifier,
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                *snippets
            )
        )
    }
}

fun ResultActions.andDocument(identifier: String, vararg snippets: Snippet) {
    "memberRole" type ENUM(MemberRole::class)
    andDo(print())
    andDo(
        document(
            identifier,
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            *snippets
        )
    )
}
