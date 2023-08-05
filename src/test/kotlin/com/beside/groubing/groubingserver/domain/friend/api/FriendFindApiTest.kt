package com.beside.groubing.groubingserver.domain.friend.api

import com.beside.groubing.groubingserver.config.ApiTest
import com.beside.groubing.groubingserver.docs.NUMBER
import com.beside.groubing.groubingserver.docs.STRING
import com.beside.groubing.groubingserver.docs.andDocument
import com.beside.groubing.groubingserver.docs.responseBody
import com.beside.groubing.groubingserver.docs.responseType
import com.beside.groubing.groubingserver.domain.friend.application.FriendFindService
import com.beside.groubing.groubingserver.domain.friend.payload.response.FriendResponse
import com.beside.groubing.groubingserver.extension.getHttpHeaderJwt
import com.beside.groubing.groubingserver.global.response.ApiResponse
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.alphanumeric
import io.kotest.property.arbitrary.email
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.of
import io.kotest.property.arbitrary.single
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.stringPattern
import io.mockk.every
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@ApiTest
@WebMvcTest(controllers = [FriendFindApi::class])
class FriendFindApiTest(
    private val mockMvc: MockMvc,
    private val mapper: ObjectMapper,
    @MockkBean private val friendFindService: FriendFindService
) : BehaviorSpec({
    Given("유저가") {
        val id = Arb.long(1L..100L).single()

        When("현재 연결된 친구 목록을") {
            val friend = Arb.of(
                FriendResponse(
                    id = Arb.long(1L..100L).single(),
                    email = Arb.email(
                        Arb.string(5, 10, Codepoint.alphanumeric()),
                        Arb.stringPattern("groubing\\.com")
                    ).single(),
                    nickname = Arb.string(2, 7, codepoints = Codepoint.alphanumeric()).single(),
                    profileUrl = null
                )
            )
            val response = listOf(friend.single())
            every { friendFindService.findById(any()) } returns response

            Then("조회한다.") {
                mockMvc.get("/api/friends") {
                    header("Authorization", getHttpHeaderJwt(id))
                    contentType = MediaType.APPLICATION_JSON
                    accept = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isOk() }
                    content { json(mapper.writeValueAsString(ApiResponse.OK(response))) }
                }.andDocument(
                    "friend-find",
                    responseBody(
                        "[].id" responseType NUMBER means "유저 ID" example "1",
                        "[].email" responseType STRING means "유저 이메일" example "test@groubing.com",
                        "[].nickname" responseType STRING means "유저 닉네임" example "그루빙멤버",
                        "[].profileUrl" responseType STRING means "프로필 이미지 URL" isOptional true,
                    )
                )
            }
        }
    }
})
