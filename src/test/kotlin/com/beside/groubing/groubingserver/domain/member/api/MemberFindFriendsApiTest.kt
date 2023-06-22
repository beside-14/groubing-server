package com.beside.groubing.groubingserver.domain.member.api

import com.beside.groubing.groubingserver.config.ApiTest
import com.beside.groubing.groubingserver.docs.NUMBER
import com.beside.groubing.groubingserver.docs.STRING
import com.beside.groubing.groubingserver.docs.andDocument
import com.beside.groubing.groubingserver.docs.pathVariables
import com.beside.groubing.groubingserver.docs.requestParam
import com.beside.groubing.groubingserver.docs.responseBody
import com.beside.groubing.groubingserver.docs.responseType
import com.beside.groubing.groubingserver.domain.member.application.MemberFindFriendsService
import com.beside.groubing.groubingserver.domain.member.payload.response.MemberDetailResponse
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ApiTest
@WebMvcTest(controllers = [MemberFindFriendsApi::class])
class MemberFindFriendsApiTest(
    private val mockMvc: MockMvc,
    private val mapper: ObjectMapper,
    @MockkBean private val memberFindFriendsService: MemberFindFriendsService
) : BehaviorSpec({
    Given("유저가") {
        val id = Arb.long(1L..100L).single()

        When("현재 연결된 친구 목록을") {
            val friend = Arb.of(
                MemberDetailResponse(
                    id = Arb.long(1L..100L).single(),
                    email = Arb.email(Arb.string(5, 10, Codepoint.alphanumeric()), Arb.stringPattern("groubing\\.com"))
                        .single(),
                    nickname = Arb.string(2, 7, codepoints = Codepoint.alphanumeric()).single(),
                    profileUrl = null
                )
            )
            val response = listOf(friend.single())
            every { memberFindFriendsService.find(any()) } returns response

            Then("조회한다.") {
                mockMvc.perform(
                    get("/api/members/{id}/friends", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getHttpHeaderJwt(id))
                ).andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(ApiResponse.OK(response))))
                    .andDocument(
                        "member-friends-find",
                        pathVariables("id" requestParam "유저 ID" example "1"),
                        responseBody(
                            "[].id" responseType NUMBER means "유저 ID" example "1",
                            "[].email" responseType STRING means "유저 이메일" example "test@groubing.com",
                            "[].nickname" responseType STRING means "유저 닉네임" example "그루빙멤버",
                            "[].profileUrl" responseType STRING means "프로필 이미지 URL" isOptional true
                        )
                    )
            }
        }

        When("친구 목록에서 닉네임으로") {
            val nickname = Arb.string(2, 7, codepoints = Codepoint.alphanumeric()).single()
            val friend = Arb.of(
                MemberDetailResponse(
                    id = Arb.long(1L..100L).single(),
                    email = Arb.email(Arb.string(5, 10, Codepoint.alphanumeric()), Arb.stringPattern("groubing\\.com"))
                        .single(),
                    nickname = nickname,
                    profileUrl = null
                )
            )
            val response = listOf(friend.single())
            every { memberFindFriendsService.findByNickname(any(), any()) } returns response

            Then("검색한다.") {
                mockMvc.perform(
                    get("/api/members/{id}/friends", id)
                        .param("nickname", nickname)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getHttpHeaderJwt(id))
                ).andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(ApiResponse.OK(response))))
                    .andDocument(
                        "member-friends-nickname-find",
                        pathVariables("id" requestParam "유저 ID" example "1"),
                        requestParam("nickname" requestParam "검색할 닉네임" example nickname),
                        responseBody(
                            "[].id" responseType NUMBER means "유저 ID" example "1",
                            "[].email" responseType STRING means "유저 이메일" example "test@groubing.com",
                            "[].nickname" responseType STRING means "유저 닉네임" example "그루빙멤버",
                            "[].profileUrl" responseType STRING means "프로필 이미지 URL" isOptional true
                        )
                    )
            }
        }
    }
})
