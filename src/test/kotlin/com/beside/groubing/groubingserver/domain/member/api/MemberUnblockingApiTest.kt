package com.beside.groubing.groubingserver.domain.member.api

import com.beside.groubing.groubingserver.config.ApiTest
import com.beside.groubing.groubingserver.docs.andDocument
import com.beside.groubing.groubingserver.docs.pathVariables
import com.beside.groubing.groubingserver.docs.requestParam
import com.beside.groubing.groubingserver.domain.member.application.MemberUnblockingService
import com.beside.groubing.groubingserver.extension.getHttpHeaderJwt
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.single
import io.mockk.justRun
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ApiTest
@WebMvcTest(controllers = [MemberUnblockingApi::class])
class MemberUnblockingApiTest(
    private val mockMvc: MockMvc,
    @MockkBean private val memberUnblockingService: MemberUnblockingService
) : BehaviorSpec({
    Given("유저가") {
        val id = Arb.long(1L..100L).single()
        val blockedMemberId = Arb.long(1L..100L).single()

        When("특정 유저를") {
            justRun { memberUnblockingService.unblocking(any()) }

            Then("차단 해제한다.") {
                mockMvc.perform(
                    delete("/api/members/{requesterId}/blocked-list/{id}", id, blockedMemberId)
                        .header("Authorization", getHttpHeaderJwt(id))
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk)
                    .andDocument(
                        "member-unblocking",
                        pathVariables(
                            "requesterId" requestParam "현재 로그인한 유저 ID" example id.toString(),
                            "id" requestParam "회원 차단 내역 ID" example blockedMemberId.toString()
                        )
                    )
            }
        }
    }
})
