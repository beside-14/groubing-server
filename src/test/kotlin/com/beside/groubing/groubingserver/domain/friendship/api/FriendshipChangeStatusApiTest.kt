package com.beside.groubing.groubingserver.domain.friendship.api

import com.beside.groubing.groubingserver.config.ApiTest
import com.beside.groubing.groubingserver.docs.BOOLEAN
import com.beside.groubing.groubingserver.docs.andDocument
import com.beside.groubing.groubingserver.docs.pathVariables
import com.beside.groubing.groubingserver.docs.requestBody
import com.beside.groubing.groubingserver.docs.requestParam
import com.beside.groubing.groubingserver.docs.requestType
import com.beside.groubing.groubingserver.domain.friendship.application.FriendshipChangeStatusService
import com.beside.groubing.groubingserver.domain.friendship.payload.request.FriendshipChangeStatusRequest
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ApiTest
@WebMvcTest(controllers = [FriendshipChangeStatusApi::class])
class FriendshipChangeStatusApiTest(
    private val mockMvc: MockMvc,
    private val mapper: ObjectMapper,
    @MockkBean private val friendshipChangeStatusService: FriendshipChangeStatusService
) : BehaviorSpec({
    Given("유저가") {
        val id = Arb.long(1L..100L).single()
        val friendshipId = Arb.long(1L..100L).single()
        val request = FriendshipChangeStatusRequest(true)

        When("친구 요청을") {
            justRun { friendshipChangeStatusService.change(any(), any()) }

            Then("수락한다.") {
                mockMvc.perform(
                    patch("/api/friendships/{id}", friendshipId)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getHttpHeaderJwt(id))
                ).andExpect(status().isOk)
                    .andDocument(
                        "friendship-change-status",
                        pathVariables("id" requestParam "친구 요청 ID" example friendshipId.toString()),
                        requestBody("accept" requestType BOOLEAN means "요청 수락 여부" example true.toString())
                    )
            }
        }
    }
})
