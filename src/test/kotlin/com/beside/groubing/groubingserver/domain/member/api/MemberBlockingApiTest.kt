package com.beside.groubing.groubingserver.domain.member.api

import com.beside.groubing.groubingserver.config.ApiTest
import com.beside.groubing.groubingserver.docs.NUMBER
import com.beside.groubing.groubingserver.docs.andDocument
import com.beside.groubing.groubingserver.docs.pathVariables
import com.beside.groubing.groubingserver.docs.requestBody
import com.beside.groubing.groubingserver.docs.requestParam
import com.beside.groubing.groubingserver.docs.requestType
import com.beside.groubing.groubingserver.domain.member.application.MemberBlockingService
import com.beside.groubing.groubingserver.domain.member.payload.request.MemberBlockingRequest
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
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ApiTest
@WebMvcTest(controllers = [MemberBlockingApi::class])
class MemberBlockingApiTest(
    private val mockMvc: MockMvc,
    private val mapper: ObjectMapper,
    @MockkBean private val memberBlockingService: MemberBlockingService
) : BehaviorSpec({
    Given("유저가") {
        val id = Arb.long(1L..100L).single()
        val request = MemberBlockingRequest(Arb.long(1L..100L).single())

        When("특정 유저를") {
            justRun { memberBlockingService.blocking(any(), any()) }

            Then("차단한다.") {
                mockMvc.perform(
                    post("/api/members/{requesterId}/blocked-list", id)
                        .header("Authorization", getHttpHeaderJwt(id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                ).andExpect(status().isOk)
                    .andDocument(
                        "member-blocking",
                        pathVariables("requesterId" requestParam "현재 로그인한 유저 ID" example id.toString()),
                        requestBody("targetMemberId" requestType NUMBER means "친구 요청할 유저 ID" example request.targetMemberId.toString())
                    )
            }
        }
    }
})
