package com.beside.groubing.domain.member.api

import com.beside.groubing.config.ApiTest
import com.beside.groubing.docs.STRING
import com.beside.groubing.docs.andDocument
import com.beside.groubing.docs.requestBody
import com.beside.groubing.docs.requestType
import com.beside.groubing.docs.responseBody
import com.beside.groubing.domain.auth.application.SocialLoginService
import com.beside.groubing.domain.auth.domain.AuthenticatedMember
import com.beside.groubing.domain.auth.domain.SocialType
import com.beside.groubing.domain.member.domain.Member
import com.beside.groubing.domain.member.domain.MemberRole
import com.beside.groubing.domain.member.domain.MemberType
import com.beside.groubing.domain.member.payload.request.SocialLoginRequest
import com.beside.groubing.extension.getJwt
import com.beside.groubing.vocabulary.accessToken
import com.beside.groubing.vocabulary.email
import com.beside.groubing.vocabulary.hasNickname
import com.beside.groubing.vocabulary.memberId
import com.beside.groubing.vocabulary.nickname
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
@WebMvcTest(controllers = [SocialLoginApi::class])
class SocialLoginApiTest(
    private val mockMvc: MockMvc,
    private val mapper: ObjectMapper,
    @MockkBean private val socialLoginService: SocialLoginService
) : BehaviorSpec({
    Given("유저가") {
        val id = "153262439"
        val email = "test@groubing.com"
        val nickname = Arb.string(codepoints = Codepoint.alphanumeric()).single()
        val fcmToken = "cFypG01m0s:APA91bEETmrwFTfkpscX3_qpYx03NE"
        val request = SocialLoginRequest(id, email, SocialType.KAKAO, fcmToken)

        When("올바른 정보로 로그인 요청 시") {
            val jwt = getJwt(1L)
            val member = Member(
                id = 1L,
                email = email,
                password = "",
                nickname = nickname,
                role = MemberRole.MEMBER,
                memberType = MemberType.SOCIAL,
                fcmToken = fcmToken,
                notificationReceive = true,
                active = true,
                profileUrl = null
            )
            every { socialLoginService.login(any()) } returns AuthenticatedMember(member, jwt)

            Then("성공 응답을 리턴한다.") {
                mockMvc.post("/api/members/social-login") {
                    content = mapper.writeValueAsString(request)
                    contentType = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isOk() }
                }.andDocument(
                    "member-social-login-success",
                    requestBody(
                        "id" requestType STRING means "소셜 Oauth 고유 id" example id,
                        "email" requestType STRING means "유저 이메일" example email,
                        "socialType" requestType STRING means "소셜 타입" example "Kakao",
                        "fcmToken" requestType STRING means "FCM Token" example fcmToken
                    ),
                    responseBody(
                        memberId("id", "유저 ID"),
                        email(),
                        nickname() formattedAs "^[가-힣a-zA-Z0-9]{2,7}",
                        profileUrl() example "/api/files/\${fileName} 혹은 null",
                        accessToken(),
                        hasNickname()
                    )
                )

                verify(exactly = 1) { socialLoginService.login(any()) }
            }
        }
    }
})
