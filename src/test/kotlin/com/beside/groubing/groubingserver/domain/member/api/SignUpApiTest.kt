package com.beside.groubing.groubingserver.domain.member.api

import com.beside.groubing.groubingserver.docs.STRING
import com.beside.groubing.groubingserver.docs.andDocument
import com.beside.groubing.groubingserver.docs.requestBody
import com.beside.groubing.groubingserver.docs.responseBody
import com.beside.groubing.groubingserver.docs.type
import com.beside.groubing.groubingserver.domain.member.application.SignUpService
import com.beside.groubing.groubingserver.domain.member.exception.MemberInputException
import com.beside.groubing.groubingserver.domain.member.payload.request.SignUpRequest
import com.beside.groubing.groubingserver.domain.member.payload.response.SignUpResponse
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.email
import io.kotest.property.arbitrary.single
import io.kotest.property.arbitrary.string
import io.mockk.every
import io.mockk.verify
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@WithMockUser
@WebMvcTest(controllers = [SignUpApi::class])
@AutoConfigureRestDocs
class SignUpApiTest(
    private val mockMvc: MockMvc,
    private val mapper: ObjectMapper,
    @MockkBean private val signUpService: SignUpService
) : BehaviorSpec({
    Given("유저가") {
        val email = Arb.email().single()
        val password = Arb.string(18..18).single()
        val request = SignUpRequest(email, password)

        When("올바른 정보로 회원가입 요청 시") {
            val response = SignUpResponse(email)
            every { signUpService.signUp(any()) } returns response

            Then("성공 응답을 리턴한다.") {
                mockMvc.post("/api/members") {
                    content = mapper.writeValueAsString(request)
                    contentType = MediaType.APPLICATION_JSON
                    with(csrf())
                }.andExpect {
                    status { isOk() }
                }.andDocument(
                    "member-signup-success",
                    requestBody(
                        "email" type STRING means "유저 이메일" example "test@groubing.com",
                        "password" type STRING means "유저 패스워드" example "Bside-14th"
                    ),
                    responseBody(
                        "data.email" type STRING means "유저 이메일" example "test@groubing.com"
                    )
                )

                verify(exactly = 1) { signUpService.signUp(any()) }
            }
        }

        When("유효하지 않은 회원가입 요쳥 시") {
            val exception = MemberInputException("이미 가입된 email 주소입니다.")
            every { signUpService.signUp(any()) } throws exception

            Then("실패 응답을 리턴한다.") {
                mockMvc.post("/api/members") {
                    content = mapper.writeValueAsString(request)
                    contentType = MediaType.APPLICATION_JSON
                    with(csrf())
                }.andExpect {
                    status { isBadRequest() }
                }.andDocument("member-signup-fail")
            }

            verify(exactly = 1) { signUpService.signUp(any()) }
        }
    }
})
