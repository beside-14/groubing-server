package com.beside.groubing.domain.bingo.api

import com.beside.groubing.config.ApiTest
import com.beside.groubing.docs.andDocument
import com.beside.groubing.docs.pathVariables
import com.beside.groubing.docs.requestParam
import com.beside.groubing.domain.bingo.application.BingoBoardDeleteService
import com.beside.groubing.extension.getHttpHeaderJwt
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.mockk.every
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@ApiTest
@WebMvcTest(controllers = [BingoBoardDeleteApi::class])
class BingoBoardDeleteApiTest(
    private val mockMvc: MockMvc,
    @MockkBean private val bingoBoardDeleteService: BingoBoardDeleteService
) : FunSpec({
    val memberId = 1L
    test("빙고 삭제 Rest Docs Api") {
        val bingoBoardId = 100L
        every { bingoBoardDeleteService.delete(memberId = memberId, boardId = bingoBoardId) } returns Unit

        mockMvc.perform(
            RestDocumentationRequestBuilders.delete("/api/bingo-boards/{id}", bingoBoardId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", getHttpHeaderJwt(memberId))
        ).andDo(MockMvcResultHandlers.print())
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDocument(
                "delete-bingo",
                pathVariables(
                    "id" requestParam "빙고 ID" example "1" isOptional true
                )
            )
    }
})
