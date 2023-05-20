package com.beside.groubing.groubingserver.domain.member.api

import com.beside.groubing.groubingserver.config.ApiTest
import com.beside.groubing.groubingserver.docs.NUMBER
import com.beside.groubing.groubingserver.docs.STRING
import com.beside.groubing.groubingserver.docs.andDocument
import com.beside.groubing.groubingserver.docs.requestBody
import com.beside.groubing.groubingserver.docs.requestType
import com.beside.groubing.groubingserver.docs.responseBody
import com.beside.groubing.groubingserver.docs.responseType
import com.beside.groubing.groubingserver.domain.member.application.SignUpService
import com.beside.groubing.groubingserver.domain.member.domain.MemberRole
import com.beside.groubing.groubingserver.domain.member.exception.MemberInputException
import com.beside.groubing.groubingserver.domain.member.payload.request.SignUpRequest
import com.beside.groubing.groubingserver.domain.member.payload.response.MemberResponse
import com.beside.groubing.groubingserver.global.domain.security.JwtProvider
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
@WebMvcTest(controllers = [SignUpApi::class])
class SignUpApiTest(
    private val mockMvc: MockMvc,
    private val mapper: ObjectMapper,
    @MockkBean private val signUpService: SignUpService
) : BehaviorSpec({
    Given("유저가") {
        val id = 1L
        val email = "user1@gmail.com"
        val password = "abcd1234"
        val request = SignUpRequest(email, password)

        When("올바른 정보로 회원가입 요청 시") {
            val token = JwtProvider.createToken(id, email, MemberRole.MEMBER)
            val response = MemberResponse(id, email, token)
            every { signUpService.signUp(any()) } returns response

            Then("성공 응답을 리턴한다.") {
                mockMvc.post("/api/members") {
                    content = mapper.writeValueAsString(request)
                    contentType = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isOk() }
                }.andDocument(
                    "member-signup-success",
                    requestBody(
                        "email" requestType STRING means "유저 이메일" example "test@groubing.com",
                        "password" requestType STRING means "유저 패스워드" example "Bside-14th" formattedAs "^[a-zA-Z0-9!-/:-@\\[-_~]{8,20}",
                        "nickname" requestType STRING means "닉네임" example "푸른바다123" formattedAs "^[가-힣a-zA-Z0-9]{2,7}"
                    ),
                    responseBody(
                        "id" responseType NUMBER means "유저 ID" example "1",
                        "email" responseType STRING means "유저 이메일" example "test@groubing.com",
                        "token" responseType STRING means "유저 JWT 토큰"
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
                }.andExpect {
                    status { isBadRequest() }
                }.andDocument("member-signup-fail")
            }

            verify(exactly = 1) { signUpService.signUp(any()) }
        }
    }
})
