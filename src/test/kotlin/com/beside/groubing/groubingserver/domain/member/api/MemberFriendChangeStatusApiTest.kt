package com.beside.groubing.groubingserver.domain.member.api

import com.beside.groubing.groubingserver.config.ApiTest
import com.beside.groubing.groubingserver.docs.BOOLEAN
import com.beside.groubing.groubingserver.docs.andDocument
import com.beside.groubing.groubingserver.docs.pathVariables
import com.beside.groubing.groubingserver.docs.requestBody
import com.beside.groubing.groubingserver.docs.requestParam
import com.beside.groubing.groubingserver.docs.requestType
import com.beside.groubing.groubingserver.domain.member.application.MemberFriendAcceptService
import com.beside.groubing.groubingserver.domain.member.application.MemberFriendDeleteService
import com.beside.groubing.groubingserver.domain.member.payload.request.MemberFriendChangeStatusRequest
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
@WebMvcTest(controllers = [MemberFriendChangeStatusApi::class])
@MockkBean(MemberFriendDeleteService::class)
class MemberFriendChangeStatusApiTest(
    private val mockMvc: MockMvc,
    private val mapper: ObjectMapper,
    @MockkBean private val memberFriendAcceptService: MemberFriendAcceptService
) : BehaviorSpec({
    Given("유저가") {
        val id = Arb.long(1L..100L).single()
        val friendshipId = Arb.long(1L..100L).single()
        val request = MemberFriendChangeStatusRequest(true)

        When("친구 요청을") {
            justRun { memberFriendAcceptService.accept(any()) }

            Then("수락한다.") {
                mockMvc.perform(
                    patch("/api/members/{inviterId}/friends/{id}", id, friendshipId)
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", getHttpHeaderJwt(id))
                ).andExpect(status().isOk)
                    .andDocument(
                        "friendship-change-status",
                        pathVariables(
                            "inviterId" requestParam "현재 로그인한 유저 ID" example id.toString(),
                            "id" requestParam "친구 요청 ID" example friendshipId.toString()
                        ),
                        requestBody("accept" requestType BOOLEAN means "요청 수락 여부, `true` 인 경우 친구 관계가 맺어지고 `false` 인 경우 요청이 거절되며 친구 요청 데이터가 삭제됩니다." example true.toString())
                    )
            }
        }
    }
})
