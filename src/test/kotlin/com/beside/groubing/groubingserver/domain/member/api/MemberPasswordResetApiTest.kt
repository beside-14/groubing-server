package com.beside.groubing.groubingserver.domain.member.api

import com.beside.groubing.groubingserver.config.ApiTest
import com.beside.groubing.groubingserver.docs.STRING
import com.beside.groubing.groubingserver.docs.andDocument
import com.beside.groubing.groubingserver.docs.pathVariables
import com.beside.groubing.groubingserver.docs.requestBody
import com.beside.groubing.groubingserver.docs.requestParam
import com.beside.groubing.groubingserver.docs.requestType
import com.beside.groubing.groubingserver.domain.member.application.MemberPasswordResetService
import com.beside.groubing.groubingserver.domain.member.payload.request.MemberPasswordResetRequest
import com.beside.groubing.groubingserver.extension.getHttpHeaderJwt
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.alphanumeric
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.single
import io.kotest.property.arbitrary.string
import io.mockk.justRun
import io.mockk.verify
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@ApiTest
@WebMvcTest(controllers = [MemberPasswordResetApi::class])
class MemberPasswordResetApiTest(
    private val mockMvc: MockMvc,
    private val mapper: ObjectMapper,
    @MockkBean private val memberPasswordResetService: MemberPasswordResetService
) : BehaviorSpec({
    Given("유저가") {
        val id = Arb.long(min = 1L, max = 100L).single()
        val password = Arb.string(8, 20, codepoints = Codepoint.alphanumeric()).single()
        val request = MemberPasswordResetRequest(password)

        When("비밀번호를 변경하려고 하는 경우") {
            justRun { memberPasswordResetService.reset(any(), any()) }

            Then("성공 응답을 리턴한다.") {
                mockMvc.perform(
                    patch("/api/members/{id}/password", id)
                        .header("Authorization", getHttpHeaderJwt(id))
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(MockMvcResultHandlers.print())
                    .andExpect(MockMvcResultMatchers.status().isOk)
                    .andDocument(
                        "member-password-reset",
                        pathVariables(
                            "id" requestParam "회원 ID" example id.toString() isOptional true
                        ),
                        requestBody(
                            "password" requestType STRING means "유저 패스워드" example password formattedAs "^[a-zA-Z0-9!-/:-@\\[-_~]{8,20}"
                        )
                    )

                verify(exactly = 1) { memberPasswordResetService.reset(any(), any()) }
            }
        }
    }
})
