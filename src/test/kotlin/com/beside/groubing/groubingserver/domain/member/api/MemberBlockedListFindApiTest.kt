package com.beside.groubing.groubingserver.domain.member.api

import com.beside.groubing.groubingserver.config.ApiTest
import com.beside.groubing.groubingserver.docs.NUMBER
import com.beside.groubing.groubingserver.docs.STRING
import com.beside.groubing.groubingserver.docs.andDocument
import com.beside.groubing.groubingserver.docs.pathVariables
import com.beside.groubing.groubingserver.docs.requestParam
import com.beside.groubing.groubingserver.docs.responseBodyWithPage
import com.beside.groubing.groubingserver.docs.responseTypeWithPage
import com.beside.groubing.groubingserver.domain.member.application.MemberBlockedListFindService
import com.beside.groubing.groubingserver.domain.member.payload.response.MemberSummaryResponse
import com.beside.groubing.groubingserver.extension.getHttpHeaderJwt
import com.beside.groubing.groubingserver.global.response.ApiResponse
import com.beside.groubing.groubingserver.global.response.PageResponse
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
import org.springframework.data.domain.PageImpl
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ApiTest
@WebMvcTest(controllers = [MemberBlockedListFindApi::class])
class MemberBlockedListFindApiTest(
    private val mockMvc: MockMvc,
    private val mapper: ObjectMapper,
    @MockkBean private val memberBlockedListFindService: MemberBlockedListFindService
) : BehaviorSpec({
    Given("유저가") {
        val id = Arb.long(1L..100L).single()

        When("현재 차단된 회원 목록을") {
            val friend = Arb.of(
                MemberSummaryResponse(
                    id = Arb.long(1L..100L).single(),
                    email = Arb.email(
                        Arb.string(5, 10, Codepoint.alphanumeric()),
                        Arb.stringPattern("groubing\\.com")
                    ).single(),
                    nickname = Arb.string(2, 7, codepoints = Codepoint.alphanumeric()).single(),
                    profileUrl = null
                )
            )
            val response = PageImpl(listOf(friend.single()))
            every { memberBlockedListFindService.findById(any(), any()) } returns response

            Then("조회한다.") {
                mockMvc.perform(
                    get("/api/members/{requesterId}/blocked-list", id)
                        .header("Authorization", getHttpHeaderJwt(id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk)
                    .andExpect(content().json(mapper.writeValueAsString(ApiResponse.OK(PageResponse(response)))))
                    .andDocument(
                        "member-blocked-list",
                        pathVariables("requesterId" requestParam "현재 로그인한 유저 ID" example id.toString()),
                        requestParam("nickname" requestParam "검색할 닉네임" example "차단 유저의 닉네임" isOptional true),
                        responseBodyWithPage(
                            "id" responseTypeWithPage NUMBER means "유저 ID" example "1",
                            "email" responseTypeWithPage STRING means "유저 이메일" example "test@groubing.com",
                            "nickname" responseTypeWithPage STRING means "유저 닉네임" example "그루빙멤버",
                            "profileUrl" responseTypeWithPage STRING means "프로필 이미지 URL" isOptional true,
                        )
                    )
            }
        }

        When("친구 목록에서 닉네임으로") {
            val nickname = Arb.string(2, 7, codepoints = Codepoint.alphanumeric()).single()
            val friend = Arb.of(
                MemberSummaryResponse(
                    id = Arb.long(1L..100L).single(),
                    email = Arb.email(
                        Arb.string(5, 10, Codepoint.alphanumeric()),
                        Arb.stringPattern("groubing\\.com")
                    ).single(),
                    nickname = nickname,
                    profileUrl = null
                )
            )
            val response = PageImpl(listOf(friend.single()))
            every { memberBlockedListFindService.findByIdAndNickname(any(), any(), any()) } returns response

            Then("검색한다.") {
                mockMvc.perform(
                    get("/api/members/{requesterId}/blocked-list", id)
                        .param("nickname", nickname)
                        .header("Authorization", getHttpHeaderJwt(id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk)
                    .andExpect(content().json(mapper.writeValueAsString(ApiResponse.OK(PageResponse(response)))))
            }
        }
    }
})
