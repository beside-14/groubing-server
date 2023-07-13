package com.beside.groubing.groubingserver.domain.friend.api

import com.beside.groubing.groubingserver.config.ApiTest
import com.beside.groubing.groubingserver.docs.BOOLEAN
import com.beside.groubing.groubingserver.docs.andDocument
import com.beside.groubing.groubingserver.docs.pathVariables
import com.beside.groubing.groubingserver.docs.requestBody
import com.beside.groubing.groubingserver.docs.requestParam
import com.beside.groubing.groubingserver.docs.requestType
import com.beside.groubing.groubingserver.domain.friend.application.FriendAcceptService
import com.beside.groubing.groubingserver.domain.friend.application.FriendDeleteService
import com.beside.groubing.groubingserver.domain.friend.payload.request.FriendChangeStatusRequest
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
@WebMvcTest(controllers = [FriendChangeStatusApi::class])
@MockkBean(FriendDeleteService::class)
class FriendChangeStatusApiTest(
    private val mockMvc: MockMvc,
    private val mapper: ObjectMapper,
    @MockkBean private val friendAcceptService: FriendAcceptService
) : BehaviorSpec({
    Given("유저가") {
        val id = Arb.long(1L..100L).single()
        val userId = Arb.long(1L..100L).single()
        val request = FriendChangeStatusRequest(true)

        When("친구 요청을") {
            justRun { friendAcceptService.accept(any()) }

            Then("수락한다.") {
                mockMvc.perform(
                    patch("/api/friends/{id}", id)
                        .header("Authorization", getHttpHeaderJwt(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                ).andExpect(status().isOk)
                    .andDocument(
                        "friend-change-status",
                        pathVariables("id" requestParam "친구 요청 ID" example id.toString()),
                        requestBody("accept" requestType BOOLEAN means "요청 수락 여부, `true` 인 경우 친구 관계가 맺어지고 `false` 인 경우 요청이 거절되며 친구 요청 데이터가 삭제됩니다." example true.toString())
                    )
            }
        }
    }
})
