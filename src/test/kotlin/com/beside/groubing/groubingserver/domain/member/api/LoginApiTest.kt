package com.beside.groubing.groubingserver.domain.member.api

import com.beside.groubing.groubingserver.domain.member.application.LoginService
import com.beside.groubing.groubingserver.domain.member.exception.MemberInputException
import com.beside.groubing.groubingserver.domain.member.payload.request.LoginRequest
import com.beside.groubing.groubingserver.extension.andDocument
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
@WebMvcTest(controllers = [LoginApi::class])
@AutoConfigureRestDocs
class LoginApiTest(
    private val mockMvc: MockMvc,
    private val objectMapper: ObjectMapper,
    @MockkBean private val loginService: LoginService
) : BehaviorSpec({
    Given("유저가 이메일 / 비밀번호를 입력한 후") {
        val email = Arb.email().single()
        val password = Arb.string(18..18).single()
        val request = LoginRequest(email, password)
        val exception = MemberInputException("존재하지 않는 이메일 입니다.")

        When("로그인을 요청하고") {
            every { loginService.login(any()) } throws exception

            Then("유효하지 않은 경우 실패 응답을 리턴한다.") {
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
