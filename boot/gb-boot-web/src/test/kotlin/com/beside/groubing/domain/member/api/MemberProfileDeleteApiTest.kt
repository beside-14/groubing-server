package com.beside.groubing.domain.member.api

import com.beside.groubing.config.ApiTest
import com.beside.groubing.docs.andDocument
import com.beside.groubing.docs.pathVariables
import com.beside.groubing.domain.member.application.MemberProfileDeleteService
import com.beside.groubing.extension.encodedAs
import com.beside.groubing.global.domain.id.ObfuscationType
import com.beside.groubing.vocabulary.memberIdPath
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.single
import io.mockk.justRun
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ApiTest
@WebMvcTest(controllers = [MemberProfileDeleteApi::class])
class MemberProfileDeleteApiTest(
    private val mockMvc: MockMvc,
    @MockkBean private val memberProfileDeleteService: MemberProfileDeleteService
) : BehaviorSpec({
    Given("유저가") {
        val id = Arb.long(1L..100L).single()
        val encodedId = id.encodedAs(ObfuscationType.MEMBER)

        When("기존에 등록한 프로필 이미지를 삭제할 경우") {
            justRun { memberProfileDeleteService.delete(any()) }

            Then("성공 응답을 리턴한다.") {
                mockMvc.perform(delete("/api/members/{memberId}/profile", encodedId))
                    .andDo(print())
                    .andExpect(status().isOk)
                    .andDocument(
                        "member-profile-delete",
                        pathVariables(
                            memberIdPath() example encodedId
                        )
                    )
            }
        }
    }
})
