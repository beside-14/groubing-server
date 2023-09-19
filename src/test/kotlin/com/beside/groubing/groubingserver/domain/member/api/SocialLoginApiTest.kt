package com.beside.groubing.groubingserver.domain.member.api

import com.beside.groubing.groubingserver.config.ApiTest
import com.beside.groubing.groubingserver.docs.BOOLEAN
import com.beside.groubing.groubingserver.docs.NUMBER
import com.beside.groubing.groubingserver.docs.STRING
import com.beside.groubing.groubingserver.docs.andDocument
import com.beside.groubing.groubingserver.docs.requestBody
import com.beside.groubing.groubingserver.docs.requestType
import com.beside.groubing.groubingserver.docs.responseBody
import com.beside.groubing.groubingserver.docs.responseType
import com.beside.groubing.groubingserver.domain.member.application.SocialLoginService
import com.beside.groubing.groubingserver.domain.member.domain.SocialType
import com.beside.groubing.groubingserver.domain.member.payload.request.SocialLoginRequest
import com.beside.groubing.groubingserver.domain.member.payload.response.SocialMemberResponse
import com.beside.groubing.groubingserver.extension.getJwt
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
        val request = SocialLoginRequest(id, email, SocialType.KAKAO)

        When("올바른 정보로 로그인 요청 시") {
            val jwt = getJwt(1L)
            val response = SocialMemberResponse(id = 1L, email = email, nickname = nickname, profileUrl = null, token = jwt, hasNickname = false)
            every { socialLoginService.login(any()) } returns response

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
                        "socialType" requestType STRING means "소셜 타입" example "Kakao"
                    ),
                    responseBody(
                        "id" responseType NUMBER means "유저 ID" example "1",
                        "email" responseType STRING means "유저 이메일" example email,
                        "nickname" responseType STRING means "유저 닉네임" example "푸른바다123" formattedAs "^[가-힣a-zA-Z0-9]{2,7}",
                        "profileUrl" responseType STRING means "프로필 이미지 URL" example "/api/files/\${fileName} 혹은 null" isOptional true,
                        "token" responseType STRING means "유저 JWT 토큰",
                        "hasNickname" responseType BOOLEAN means "초기 닉네임 설정여부"
                    )
                )

                verify(exactly = 1) { socialLoginService.login(any()) }
            }
        }
    }
})
