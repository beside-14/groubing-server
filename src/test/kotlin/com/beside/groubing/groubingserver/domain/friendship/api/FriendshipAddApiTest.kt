package com.beside.groubing.groubingserver.domain.friendship.api

import com.beside.groubing.groubingserver.config.ApiTest
import com.beside.groubing.groubingserver.docs.NUMBER
import com.beside.groubing.groubingserver.docs.andDocument
import com.beside.groubing.groubingserver.docs.requestBody
import com.beside.groubing.groubingserver.docs.requestType
import com.beside.groubing.groubingserver.domain.friendship.application.FriendshipAddService
import com.beside.groubing.groubingserver.domain.friendship.payload.request.FriendshipAddRequest
import com.beside.groubing.groubingserver.extension.getHttpHeaderJwt
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
@WebMvcTest(controllers = [FriendshipAddApi::class])
class FriendshipAddApiTest(
    private val mockMvc: MockMvc,
    private val mapper: ObjectMapper,
    @MockkBean private val friendshipAddService: FriendshipAddService
) : BehaviorSpec({
    Given("유저가") {
        val id = Arb.long(1L..100L).single()
        val request = FriendshipAddRequest(Arb.long(1L..100L).single())

        When("특정 유저에게") {
            justRun { friendshipAddService.add(any(), any()) }

            Then("친구 신청을 요청한다.") {
                mockMvc.post("/api/friendships") {
                    header("Authorization", getHttpHeaderJwt(id))
                    content = mapper.writeValueAsString(request)
                    contentType = MediaType.APPLICATION_JSON
                    accept = MediaType.APPLICATION_JSON
                }.andExpect { status { isOk() } }
                    .andDocument(
                        "friendship-add",
                        requestBody("inviteeId" requestType NUMBER means "친구 요청할 유저 ID" example request.inviteeId.toString())
                    )
            }
        }
    }
})
