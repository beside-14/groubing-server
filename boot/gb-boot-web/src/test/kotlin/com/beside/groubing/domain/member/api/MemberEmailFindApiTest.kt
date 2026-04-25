package com.beside.groubing.domain.member.api

import com.beside.groubing.config.ApiTest
import com.beside.groubing.docs.NUMBER
import com.beside.groubing.docs.STRING
import com.beside.groubing.docs.andDocument
import com.beside.groubing.docs.requestBody
import com.beside.groubing.docs.requestType
import com.beside.groubing.docs.responseBody
import com.beside.groubing.docs.responseType
import com.beside.groubing.domain.auth.application.MemberEmailFindService
import com.beside.groubing.domain.member.domain.Member
import com.beside.groubing.domain.member.domain.MemberRole
import com.beside.groubing.domain.member.domain.MemberType
import com.beside.groubing.domain.member.payload.request.MemberEmailFindRequest
import com.beside.groubing.domain.member.payload.response.MemberEmailFindResponse
import com.beside.groubing.global.response.ApiResponse
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
            val member = Member(
                id = id,
                email = email,
                password = "",
                nickname = "",
                role = MemberRole.MEMBER,
                memberType = MemberType.CLASSIC,
                fcmToken = null,
                notificationReceive = true,
                active = true,
                profileUrl = null
            )
            val response = MemberEmailFindResponse.of(member)
            every { memberEmailFindService.find(any()) } returns member

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
                    "member-email-find",
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
