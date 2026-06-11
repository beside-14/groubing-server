package com.beside.groubing.domain.friend.api

import com.beside.groubing.config.ApiTest
import com.beside.groubing.docs.andDocument
import com.beside.groubing.docs.responseBody
import com.beside.groubing.domain.friend.application.FriendFindService
import com.beside.groubing.domain.friend.domain.FriendMember
import com.beside.groubing.domain.friend.domain.FriendStatus
import com.beside.groubing.domain.friend.payload.response.FriendRequestResponse
import com.beside.groubing.domain.friend.payload.response.FriendResponse
import com.beside.groubing.extension.getHttpHeaderJwt
import com.beside.groubing.global.response.ApiResponse
import com.beside.groubing.vocabulary.friendId
import com.beside.groubing.vocabulary.friendStatus
import com.beside.groubing.vocabulary.memberId
import com.beside.groubing.vocabulary.nickname
import com.beside.groubing.vocabulary.profileUrl
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.alphanumeric
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.single
import io.kotest.property.arbitrary.string
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
            val friendMember = aFriendMember(FriendStatus.ACCEPT)
            val serviceResult = listOf(friendMember)
            val expectedResponse = serviceResult.map(FriendResponse::of)
            every { friendFindService.findAllAcceptedOf(any()) } returns serviceResult

            Then("조회한다.") {
                mockMvc.get("/api/friends") {
                    header("Authorization", getHttpHeaderJwt(id))
                    contentType = MediaType.APPLICATION_JSON
                    accept = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isOk() }
                    content { json(mapper.writeValueAsString(ApiResponse.OK(expectedResponse))) }
                }.andDocument(
                    "friend-find",
                    responseBody(
                        friendId("[].id"),
                        memberId("[].memberId", "유저 ID"),
                        nickname("[].nickname"),
                        profileUrl("[].profileUrl"),
                    )
                )
            }
        }

        When("초대받은 친구 요청 목록을") {
            val friendMember = aFriendMember(FriendStatus.PENDING)
            val serviceResult = listOf(friendMember)
            val expectedResponse = serviceResult.map(FriendRequestResponse::of)
            every { friendFindService.findAllReceivedPendingBy(any()) } returns serviceResult

            Then("조회한다.") {
                mockMvc.get("/api/friends/received-requests") {
                    header("Authorization", getHttpHeaderJwt(id))
                    contentType = MediaType.APPLICATION_JSON
                    accept = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isOk() }
                    content { json(mapper.writeValueAsString(ApiResponse.OK(expectedResponse))) }
                }.andDocument(
                    "friend-find-request",
                    responseBody(
                        friendId("[].id"),
                        memberId("[].memberId", "유저 ID"),
                        nickname("[].nickname"),
                        profileUrl("[].profileUrl"),
                        friendStatus("[].status"),
                    )
                )
            }
        }

        When("초대한 친구 요청 중 대기 상태인 목록을") {
            val friendMember = aFriendMember(FriendStatus.PENDING)
            val serviceResult = listOf(friendMember)
            val expectedResponse = serviceResult.map(FriendRequestResponse::of)
            every { friendFindService.findAllSentPendingBy(any()) } returns serviceResult

            Then("조회한다.") {
                mockMvc.get("/api/friends/send-requests") {
                    header("Authorization", getHttpHeaderJwt(id))
                    contentType = MediaType.APPLICATION_JSON
                    accept = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isOk() }
                    content { json(mapper.writeValueAsString(ApiResponse.OK(expectedResponse))) }
                }.andDocument(
                    "friend-find-request",
                    responseBody(
                        friendId("[].id"),
                        memberId("[].memberId", "유저 ID"),
                        nickname("[].nickname"),
                        profileUrl("[].profileUrl"),
                        friendStatus("[].status"),
                    )
                )
            }
        }
    }
})

private fun aFriendMember(status: FriendStatus): FriendMember = FriendMember(
    friendId = Arb.long(1L..100L).single(),
    memberId = Arb.long(1L..100L).single(),
    nickname = Arb.string(2, 7, codepoints = Codepoint.alphanumeric()).single(),
    profileFileName = null,
    status = status
)
