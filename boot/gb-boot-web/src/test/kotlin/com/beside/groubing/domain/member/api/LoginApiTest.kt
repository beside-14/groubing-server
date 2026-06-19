package com.beside.groubing.domain.member.api

import com.beside.groubing.config.ApiTest
import com.beside.groubing.docs.STRING
import com.beside.groubing.docs.andDocument
import com.beside.groubing.docs.requestBody
import com.beside.groubing.docs.requestType
import com.beside.groubing.docs.responseBody
import com.beside.groubing.domain.auth.application.LoginService
import com.beside.groubing.domain.auth.domain.AuthenticatedMember
import com.beside.groubing.domain.member.domain.Member
import com.beside.groubing.domain.member.domain.MemberRole
import com.beside.groubing.domain.member.domain.MemberType
import com.beside.groubing.domain.member.exception.MemberInputException
import com.beside.groubing.domain.member.payload.request.LoginRequest
import com.beside.groubing.extension.getJwt
import com.beside.groubing.vocabulary.accessToken
import com.beside.groubing.vocabulary.memberId
import com.beside.groubing.vocabulary.nickname
import com.beside.groubing.vocabulary.notificationReceive
import com.beside.groubing.vocabulary.profileUrl
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.alphanumeric
import io.kotest.property.arbitrary.single
import io.kotest.property.arbitrary.string
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
        val loginId = "groubing01"
        val nickname = Arb.string(codepoints = Codepoint.alphanumeric()).single()
        val password = "abcd1234"
        val fcmToken = "cFypG01m0s:APA91bEETmrwFTfkpscX3_qpYx03NE"
        val request = LoginRequest(loginId, password, fcmToken)

        When("올바른 정보로 로그인 요청 시") {
            val jwt = getJwt(1L)
            val member = Member(
                id = 1L,
                loginId = loginId,
                password = "",
                nickname = nickname,
                role = MemberRole.MEMBER,
                memberType = MemberType.CLASSIC,
                fcmToken = fcmToken,
                notificationReceive = true,
                active = true,
                deletedAt = null,
                profileUrl = null
            )
            every { loginService.login(any()) } returns AuthenticatedMember(member, jwt)

            Then("성공 응답을 리턴한다.") {
                mockMvc.post("/api/members/login") {
                    content = mapper.writeValueAsString(request)
                    contentType = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isOk() }
                }.andDocument(
                    "member-login-success",
                    requestBody(
                        "loginId" requestType STRING means "유저 아이디" example "groubing01",
                        "password" requestType STRING means "유저 패스워드" example "Bside-14th",
                        "fcmToken" requestType STRING means "FCM Token" example fcmToken
                    ),
                    responseBody(
                        memberId("id", "유저 ID"),
                        nickname() formattedAs "^[가-힣a-zA-Z0-9]{2,7}",
                        profileUrl() example "/api/files/\${fileName} 혹은 null",
                        accessToken(),
                        notificationReceive() formattedAs "true|false"
                    )
                )

                verify(exactly = 1) { loginService.login(any()) }
            }
        }

        When("유효하지 않은 로그인 요청 시") {
            val exception = MemberInputException("존재하지 않는 아이디 입니다.")
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
