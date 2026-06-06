package com.beside.groubing.domain.friend.api

import com.beside.groubing.config.ApiTest
import com.beside.groubing.docs.STRING
import com.beside.groubing.docs.andDocument
import com.beside.groubing.docs.requestBody
import com.beside.groubing.docs.requestType
import com.beside.groubing.domain.friend.application.FriendAddService
import com.beside.groubing.domain.friend.payload.request.FriendAddRequest
import com.beside.groubing.extension.encodedAs
import com.beside.groubing.extension.getHttpHeaderJwt
import com.beside.groubing.domain.common.id.ObfuscationType
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.single
import io.mockk.justRun
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@ApiTest
@WebMvcTest(controllers = [FriendAddApi::class])
class FriendAddApiTest(
    private val mockMvc: MockMvc,
    private val mapper: ObjectMapper,
    @MockkBean private val friendAddService: FriendAddService
) : BehaviorSpec({
    Given("유저가") {
        val id = Arb.long(1L..100L).single()
        val request = FriendAddRequest(Arb.long(1L..100L).single())

        When("특정 유저에게") {
            justRun { friendAddService.add(any(), any()) }

            Then("친구 신청을 요청한다.") {
                mockMvc.post("/api/friends") {
                    header("Authorization", getHttpHeaderJwt(id))
                    contentType = MediaType.APPLICATION_JSON
                    accept = MediaType.APPLICATION_JSON
                    content = mapper.writeValueAsString(request)
                }.andExpect { status { isOk() } }
                    .andDocument(
                        "friend-add",
                        requestBody(
                            "inviteeId" requestType STRING means
                                "친구 요청할 유저 ID (obfuscated)" example
                                request.inviteeId.encodedAs(ObfuscationType.MEMBER)
                        )
                    )
            }
        }
    }
})
