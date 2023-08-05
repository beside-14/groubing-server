package com.beside.groubing.groubingserver.domain.friend.api

import com.beside.groubing.groubingserver.config.ApiTest
import com.beside.groubing.groubingserver.docs.andDocument
import com.beside.groubing.groubingserver.docs.pathVariables
import com.beside.groubing.groubingserver.docs.requestParam
import com.beside.groubing.groubingserver.domain.friend.application.FriendAcceptService
import com.beside.groubing.groubingserver.extension.getHttpHeaderJwt
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
@WebMvcTest(controllers = [FriendAcceptApi::class])
class FriendAcceptApiTest(
    private val mockMvc: MockMvc,
    @MockkBean private val friendAcceptService: FriendAcceptService
) : BehaviorSpec({
    Given("유저가") {
        val id = Arb.long(1L..100L).single()
        val userId = Arb.long(1L..100L).single()

        When("친구 요청을") {

            Then("수락한다.") {
                justRun { friendAcceptService.accept(any(), any()) }

                mockMvc.perform(
                    patch("/api/friends/{id}/accept", id)
                        .header("Authorization", getHttpHeaderJwt(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk)
                    .andDocument(
                        "friend-accept",
                        pathVariables("id" requestParam "친구 요청 ID" example id.toString())
                    )
            }
        }
    }
})
