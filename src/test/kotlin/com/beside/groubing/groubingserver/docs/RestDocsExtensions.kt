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
import org.springframework.restdocs.request.RequestDocumentation.queryParameters
import org.springframework.restdocs.request.RequestDocumentation.requestParts
import org.springframework.restdocs.request.RequestPartDescriptor
import org.springframework.restdocs.snippet.Snippet
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.ResultActionsDsl
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print

fun requestParam(vararg fields: CustomDescriptor<ParameterDescriptor>): Snippet {
    return queryParameters(fields.map { it.descriptor as ParameterDescriptor })
}

fun requestBody(vararg fields: CustomDescriptor<FieldDescriptor>): Snippet {
    return requestFields(fields.map { it.descriptor as FieldDescriptor })
}

fun responseBody(vararg fields: CustomDescriptor<FieldDescriptor>): Snippet {
    return createResponseBody(*fields)
}

fun responseBodyWithPage(vararg fields: CustomDescriptor<FieldDescriptor>): Snippet {
    return createResponseBody(*fields) {
        mutableListOf(
            ("totalElements" responseType NUMBER means "총 데이터 개수"),
            ("numberOfElements" responseType NUMBER means ""),
            ("currentPage" responseType NUMBER means "현재 페이지 번호"),
            ("totalPages" responseType NUMBER means "총 페이지 개수"),
            ("size" responseType NUMBER means "페이지에 포함된 데이터 개수"),
            ("first" responseType BOOLEAN means "현재 페이지가 첫 페이지인지 여부"),
            ("last" responseType BOOLEAN means "현재 페이지가 마지막 페이지인지 여부")
        )
    }
}

private fun createResponseBody(
    vararg fields: CustomDescriptor<FieldDescriptor>,
    addFieldFunc: (() -> MutableList<CustomDescriptor<FieldDescriptor>>)? = null
): Snippet {
    val snippets = (fields.toMutableList() +
        ("code" responseType STRING means "Http 응답 코드") +
        ("message" responseType STRING means "Http 응답 메세지")).toMutableList()
    if (addFieldFunc != null) {
        snippets += addFieldFunc.invoke()
    }
    return responseFields(snippets.map { it.descriptor as FieldDescriptor })
}

fun pathVariables(vararg paths: CustomDescriptor<ParameterDescriptor>): Snippet =
    pathParameters(paths.map { it.descriptor as ParameterDescriptor })

fun requestParts(vararg fields: CustomDescriptor<RequestPartDescriptor>): Snippet {
    return requestParts(fields.map { it.descriptor as RequestPartDescriptor })
}

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
    "memberRole" responseType ENUM(MemberRole::class)
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
