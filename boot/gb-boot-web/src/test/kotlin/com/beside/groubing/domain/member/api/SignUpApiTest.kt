package com.beside.groubing.domain.member.api

import com.beside.groubing.config.ApiTest
import com.beside.groubing.docs.STRING
import com.beside.groubing.docs.andDocument
import com.beside.groubing.docs.requestBody
import com.beside.groubing.docs.requestType
import com.beside.groubing.docs.responseBody
import com.beside.groubing.domain.auth.application.SignUpService
import com.beside.groubing.domain.auth.domain.AuthenticatedMember
import com.beside.groubing.domain.member.domain.Member
import com.beside.groubing.domain.member.domain.MemberRole
import com.beside.groubing.domain.member.domain.MemberType
import com.beside.groubing.domain.member.exception.MemberInputException
import com.beside.groubing.domain.member.payload.request.SignUpRequest
import com.beside.groubing.global.domain.security.JwtProvider
import com.beside.groubing.vocabulary.accessToken
import com.beside.groubing.vocabulary.memberId
import com.beside.groubing.vocabulary.nickname
import com.beside.groubing.vocabulary.notificationReceive
import com.beside.groubing.vocabulary.profileUrl
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
        val loginId = "groubing01"
        val password = "abcd1234"
        val nickname = "푸른바다123"
        val request = SignUpRequest(loginId, password, nickname)

        When("올바른 정보로 회원가입 요청 시") {
            val token = JwtProvider.createToken(id, MemberRole.MEMBER.name)
            val member = Member(
                id = id,
                loginId = loginId,
                password = "",
                nickname = nickname,
                role = MemberRole.MEMBER,
                memberType = MemberType.CLASSIC,
                fcmToken = null,
                notificationReceive = true,
                active = true,
                deletedAt = null,
                profileUrl = null
            )
            every { signUpService.signUp(any()) } returns AuthenticatedMember(member, token)

            Then("성공 응답을 리턴한다.") {
                mockMvc.post("/api/members") {
                    content = mapper.writeValueAsString(request)
                    contentType = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isOk() }
                }.andDocument(
                    "member-signup-success",
                    requestBody(
                        "loginId" requestType STRING means "유저 아이디" example "groubing01" formattedAs "^[a-z0-9]{4,20}$",
                        "password" requestType STRING means "유저 패스워드" example "Bside-14th" formattedAs "^[a-zA-Z0-9!-/:-@\\[-_~]{8,20}",
                        "nickname" requestType STRING means "닉네임" example "푸른바다123" formattedAs "^[가-힣a-zA-Z0-9]{2,7}"
                    ),
                    responseBody(
                        memberId("id", "유저 ID"),
                        nickname() formattedAs "^[가-힣a-zA-Z0-9]{2,7}",
                        profileUrl() example "/api/files/\${fileName} 혹은 null",
                        accessToken(),
                        notificationReceive() formattedAs "true|false"
                    )
                )

                verify(exactly = 1) { signUpService.signUp(any()) }
            }
        }

        When("유효하지 않은 회원가입 요쳥 시") {
            val exception = MemberInputException("이미 사용 중인 아이디입니다.")
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
