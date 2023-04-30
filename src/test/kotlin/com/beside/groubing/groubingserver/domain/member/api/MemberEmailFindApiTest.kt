package com.beside.groubing.groubingserver.domain.member.api

import com.beside.groubing.groubingserver.config.ApiTest
import com.beside.groubing.groubingserver.docs.NUMBER
import com.beside.groubing.groubingserver.docs.STRING
import com.beside.groubing.groubingserver.docs.andDocument
import com.beside.groubing.groubingserver.docs.requestBody
import com.beside.groubing.groubingserver.docs.requestType
import com.beside.groubing.groubingserver.docs.responseBody
import com.beside.groubing.groubingserver.docs.responseType
import com.beside.groubing.groubingserver.domain.member.application.MemberEmailFindService
import com.beside.groubing.groubingserver.domain.member.payload.request.MemberEmailFindRequest
import com.beside.groubing.groubingserver.domain.member.payload.response.MemberEmailFindResponse
import com.beside.groubing.groubingserver.global.response.ApiResponse
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.alphanumeric
import io.kotest.property.arbitrary.email
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.single
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.stringPattern
import io.mockk.every
import io.mockk.verify
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@ApiTest
@WebMvcTest(controllers = [MemberEmailFindApi::class])
class MemberEmailFindApiTest(
    private val mockMvc: MockMvc,
    private val mapper: ObjectMapper,
    @MockkBean private val memberEmailFindService: MemberEmailFindService
) : BehaviorSpec({
    Given("유저가") {
        val id = Arb.long(min = 1L, max = 100L).single()
        val email = Arb.email(Arb.string(5, 10, Codepoint.alphanumeric()), Arb.stringPattern("groubing\\.com")).single()
        val request = MemberEmailFindRequest(email)

        When("비밀번호를 찾기 위해 이메일을 입력한 경우") {
            val response = MemberEmailFindResponse(id, email)
            every { memberEmailFindService.find(any()) } returns response

            Then("성공 응답을 리턴한다.") {
                mockMvc.post("/api/members/find-email") {
                    content = mapper.writeValueAsString(request)
                    contentType = MediaType.APPLICATION_JSON
                }.andExpect {
                    status {
                        isOk()
                        content { json(mapper.writeValueAsString(ApiResponse.OK(response))) }
                    }
                }.andDocument(
                    "member-find-email",
                    requestBody(
                        "email" requestType STRING means "유저 이메일" example email
                    ),
                    responseBody(
                        "id" responseType NUMBER means "유저 ID" example id.toString(),
                        "email" responseType STRING means "유저 이메일" example email
                    )
                )

                verify(exactly = 1) { memberEmailFindService.find(any()) }
            }
        }
    }
})
