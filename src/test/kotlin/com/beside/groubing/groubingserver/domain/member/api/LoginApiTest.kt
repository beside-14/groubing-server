package com.beside.groubing.groubingserver.domain.member.api

import com.beside.groubing.groubingserver.domain.member.application.LoginService
import com.beside.groubing.groubingserver.domain.member.exception.MemberInputException
import com.beside.groubing.groubingserver.domain.member.payload.request.LoginRequest
import com.beside.groubing.groubingserver.domain.member.payload.response.LoginResponse
import com.beside.groubing.groubingserver.extension.andDocument
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.email
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.single
import io.kotest.property.arbitrary.string
import io.mockk.every
import io.mockk.verify
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath
import org.springframework.restdocs.payload.PayloadDocumentation.requestFields
import org.springframework.restdocs.payload.PayloadDocumentation.responseFields
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@WithMockUser
@WebMvcTest(controllers = [LoginApi::class])
@AutoConfigureRestDocs
class LoginApiTest(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
    @MockkBean private val loginService: LoginService
) : BehaviorSpec({
    Given("유저가") {
        val email = Arb.email().single()
        val password = Arb.string(18..18).single()
        val request = LoginRequest(email, password)

        When("올바른 정보로 로그인 요청 시") {
            val response = LoginResponse(Arb.long().single(), email, Arb.string().single())
            every { loginService.login(any()) } returns response

            Then("성공 응답을 리턴한다.") {
                mockMvc.post("/api/members/login") {
                    content = objectMapper.writeValueAsString(request)
                    contentType = MediaType.APPLICATION_JSON
                    with(csrf())
                }.andExpect {
                    status { isOk() }
                }.andDocument("member-login-success") {
                    arrayOf(
                        requestFields(
                            fieldWithPath("email")
                                .type(JsonFieldType.STRING)
                                .description("로그인 이메일"),
                            fieldWithPath("password")
                                .type(JsonFieldType.STRING)
                                .description("로그인 패스워드")
                        ),
                        responseFields(
                            fieldWithPath("data.id")
                                .type(JsonFieldType.NUMBER)
                                .description("유저 ID"),
                            fieldWithPath("data.email")
                                .type(JsonFieldType.STRING)
                                .description("유저 이메일"),
                            fieldWithPath("data.token")
                                .type(JsonFieldType.STRING)
                                .description("유저 JWT 토큰"),
                            fieldWithPath("code")
                                .type(JsonFieldType.STRING)
                                .description("Http 응답 코드"),
                            fieldWithPath("message")
                                .type(JsonFieldType.STRING)
                                .description("Http 응답 메세지")
                        )
                    )
                }

                verify(exactly = 1) { loginService.login(any()) }
            }
        }

        When("유효하지 않은 로그인 요청 시") {
            val exception = MemberInputException("존재하지 않는 이메일 입니다.")
            every { loginService.login(any()) } throws exception

            Then("실패 응답을 리턴한다.") {
                mockMvc.post("/api/members/login") {
                    content = objectMapper.writeValueAsString(request)
                    contentType = MediaType.APPLICATION_JSON
                    with(csrf())
                }.andExpect {
                    status { isBadRequest() }
                }.andDocument("member-login-fail")
            }

            verify(exactly = 1) { loginService.login(any()) }
        }
    }
})
