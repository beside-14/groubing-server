package com.beside.groubing.groubingserver.domain.friendship.api

import com.beside.groubing.groubingserver.config.ApiTest
import com.beside.groubing.groubingserver.docs.NUMBER
import com.beside.groubing.groubingserver.docs.STRING
import com.beside.groubing.groubingserver.docs.andDocument
import com.beside.groubing.groubingserver.docs.requestParam
import com.beside.groubing.groubingserver.docs.responseBodyWithPage
import com.beside.groubing.groubingserver.docs.responseTypeWithPage
import com.beside.groubing.groubingserver.domain.member.application.MemberFindFriendsService
import com.beside.groubing.groubingserver.domain.member.payload.response.MemberDetailResponse
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
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@ApiTest
@WebMvcTest(controllers = [FriendshipFindApi::class])
class FriendshipFindApiTest(
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
                    email = Arb.email(
                        Arb.string(5, 10, Codepoint.alphanumeric()),
                        Arb.stringPattern("groubing\\.com")
                    ).single(),
                    nickname = Arb.string(2, 7, codepoints = Codepoint.alphanumeric()).single(),
                    profileUrl = null
                )
            )
            val response = PageImpl(listOf(friend.single()))
            every { memberFindFriendsService.findById(any(), any()) } returns response

            Then("조회한다.") {
                mockMvc.get("/api/friendships") {
                    contentType = MediaType.APPLICATION_JSON
                    accept = MediaType.APPLICATION_JSON
                    header("Authorization", getHttpHeaderJwt(id))
                }.andExpect {
                    status { isOk() }
                    content { json(mapper.writeValueAsString(ApiResponse.OK(PageResponse(response)))) }
                }.andDocument(
                    "member-friends-find",
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
                MemberDetailResponse(
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
            every { memberFindFriendsService.findByIdAndNickname(any(), any(), any()) } returns response

            Then("검색한다.") {
                mockMvc.get("/api/friendships") {
                    param("nickname", nickname)
                    contentType = MediaType.APPLICATION_JSON
                    accept = MediaType.APPLICATION_JSON
                    header("Authorization", getHttpHeaderJwt(id))
                }.andExpect {
                    status { isOk() }
                    content { json(mapper.writeValueAsString(ApiResponse.OK(PageResponse(response)))) }
                }.andDocument(
                    "member-friends-nickname-find",
                    requestParam("nickname" requestParam "검색할 닉네임" example nickname),
                    responseBodyWithPage(
                        "id" responseTypeWithPage NUMBER means "유저 ID" example "1",
                        "email" responseTypeWithPage STRING means "유저 이메일" example "test@groubing.com",
                        "nickname" responseTypeWithPage STRING means "유저 닉네임" example "그루빙멤버",
                        "profileUrl" responseTypeWithPage STRING means "프로필 이미지 URL" isOptional true
                    )
                )
            }
        }
    }
})
