package com.beside.groubing.groubingserver.domain.member.api

import com.beside.groubing.groubingserver.config.ApiTest
import com.beside.groubing.groubingserver.docs.NUMBER
import com.beside.groubing.groubingserver.docs.STRING
import com.beside.groubing.groubingserver.docs.andDocument
import com.beside.groubing.groubingserver.docs.requestBody
import com.beside.groubing.groubingserver.docs.requestType
import com.beside.groubing.groubingserver.docs.responseBody
import com.beside.groubing.groubingserver.docs.responseType
import com.beside.groubing.groubingserver.domain.member.application.LoginService
import com.beside.groubing.groubingserver.domain.member.exception.MemberInputException
import com.beside.groubing.groubingserver.domain.member.payload.request.LoginRequest
import com.beside.groubing.groubingserver.domain.member.payload.response.MemberLoginResponse
import com.beside.groubing.groubingserver.extension.getJwt
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.verify
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@ApiTest
@WebMvcTest(controllers = [LoginApi::class])
class LoginApiTest(
    private val mockMvc: MockMvc,
    private val mapper: ObjectMapper,
    @MockkBean private val loginService: LoginService
) : BehaviorSpec({
    Given("유저가") {
        val email = "user1@gmail.com"
        val password = "abcd1234"
        val request = LoginRequest(email, password)

        When("올바른 정보로 로그인 요청 시") {
            val jwt = getJwt(1L)
            val response = MemberLoginResponse(1L, email, jwt)
            every { loginService.login(any()) } returns response

            Then("성공 응답을 리턴한다.") {
                mockMvc.post("/api/members/login") {
                    content = mapper.writeValueAsString(request)
                    contentType = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isOk() }
                }.andDocument(
                    "member-login-success",
                    requestBody(
                        "email" requestType STRING means "유저 이메일" example "test@groubing.com",
                        "password" requestType STRING means "유저 패스워드" example "Bside-14th"
                    ),
                    responseBody(
                        "id" responseType NUMBER means "유저 ID" example "1",
                        "email" responseType STRING means "유저 이메일" example "test@groubing.com",
                        "token" responseType STRING means "유저 JWT 토큰"
                    )
                )

                verify(exactly = 1) { loginService.login(any()) }
            }
        }

        When("유효하지 않은 로그인 요청 시") {
            val exception = MemberInputException("존재하지 않는 이메일 입니다.")
            every { loginService.login(any()) } throws exception

            Then("실패 응답을 리턴한다.") {
                mockMvc.post("/api/members/login") {
                    content = mapper.writeValueAsString(request)
                    contentType = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isBadRequest() }
                }.andDocument("member-login-fail")
            }

            verify(exactly = 1) { loginService.login(any()) }
        }
    }
})
