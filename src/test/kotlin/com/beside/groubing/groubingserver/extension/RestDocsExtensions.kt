package com.beside.groubing.groubingserver.extension

import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest
import org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse
import org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint
import org.springframework.restdocs.snippet.Snippet
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.ResultActionsDsl
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print

fun ResultActionsDsl.andDocument(identifier: String, snippets: (() -> Array<Snippet>)? = null) {
    andDo {
        print()
        handle(
            document(
                identifier,
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                *snippets?.invoke() ?: emptyArray()
            )
        )
    }
}

fun ResultActions.andDocument(identifier: String, snippets: (() -> Array<Snippet>)? = null) {
    andDo(print())
    andDo(
        document(
            identifier,
            preprocessRequest(prettyPrint()),
            preprocessResponse(prettyPrint()),
            *snippets?.invoke() ?: emptyArray()
        )
    )
}
