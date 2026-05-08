package com.beside.groubing.domain.member.api

import com.beside.groubing.config.ApiTest
import com.beside.groubing.docs.STRING
import com.beside.groubing.docs.andDocument
import com.beside.groubing.docs.pathVariables
import com.beside.groubing.docs.requestBody
import com.beside.groubing.docs.requestType
import com.beside.groubing.domain.member.application.MemberNicknameEditService
import com.beside.groubing.domain.member.payload.request.MemberNicknameEditRequest
import com.beside.groubing.extension.getHttpHeaderJwt
import com.beside.groubing.vocabulary.memberIdPath
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.alphanumeric
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.single
import io.kotest.property.arbitrary.string
import io.mockk.justRun
import io.mockk.verify
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ApiTest
@WebMvcTest(controllers = [MemberNicknameEditApi::class])
class MemberNicknameEditApiTest(
    private val mockMvc: MockMvc,
    private val mapper: ObjectMapper,
    @MockkBean private val memberNicknameEditService: MemberNicknameEditService
) : BehaviorSpec({
    Given("유저가") {
        val id = Arb.long(min = 1L, max = 100L).single()
        val nickname = Arb.string(2, 7, codepoints = Codepoint.alphanumeric()).single()
        val request = MemberNicknameEditRequest(nickname)

        When("비밀번호를 찾기 위해 이메일을 입력한 경우") {
            justRun { memberNicknameEditService.edit(any(), any()) }

            Then("성공 응답을 리턴한다.") {
                mockMvc.perform(
                    patch("/api/members/{id}/nickname", id)
                        .header("Authorization", getHttpHeaderJwt(id))
                        .content(mapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                    .andExpect(status().isOk)
                    .andDocument(
                        "member-nickname-edit",
                        pathVariables(
                            memberIdPath() example id.toString() isOptional true
                        ),
                        requestBody(
                            "nickname" requestType STRING means "닉네임" example nickname formattedAs "^[가-힣a-zA-Z0-9]{2,7}"
                        )
                    )

                verify(exactly = 1) { memberNicknameEditService.edit(any(), any()) }
            }
        }
    }
})
