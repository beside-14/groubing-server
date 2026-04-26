package com.beside.groubing.domain.blockedmember.api

import com.beside.groubing.config.ApiTest
import com.beside.groubing.docs.NUMBER
import com.beside.groubing.docs.STRING
import com.beside.groubing.docs.andDocument
import com.beside.groubing.docs.responseBody
import com.beside.groubing.docs.responseType
import com.beside.groubing.domain.blockedmember.application.BlockedMemberFindService
import com.beside.groubing.domain.blockedmember.domain.BlockedMemberTarget
import com.beside.groubing.domain.blockedmember.payload.response.BlockedMemberResponse
import com.beside.groubing.extension.getHttpHeaderJwt
import com.beside.groubing.global.response.ApiResponse
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.Codepoint
import io.kotest.property.arbitrary.alphanumeric
import io.kotest.property.arbitrary.email
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.single
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.stringPattern
import io.mockk.every
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@ApiTest
@WebMvcTest(controllers = [BlockedMemberFindApi::class])
class BlockedMemberFindApiTest(
    private val mockMvc: MockMvc,
    private val mapper: ObjectMapper,
    @MockkBean private val blockedMemberFindService: BlockedMemberFindService
) : BehaviorSpec({
    Given("유저가") {
        val id = Arb.long(1L..100L).single()

        When("현재 차단된 회원 목록을") {
            val target = BlockedMemberTarget(
                id = Arb.long(1L..100L).single(),
                email = Arb.email(
                    Arb.string(5, 10, Codepoint.alphanumeric()),
                    Arb.stringPattern("groubing\\.com")
                ).single(),
                nickname = Arb.string(2, 7, codepoints = Codepoint.alphanumeric()).single(),
                profileFileName = null
            )
            val serviceResult = listOf(target)
            val expectedResponse = serviceResult.map(BlockedMemberResponse::of)
            every { blockedMemberFindService.find(any()) } returns serviceResult

            Then("조회한다.") {
                mockMvc.get("/api/blocked-members") {
                    header("Authorization", getHttpHeaderJwt(id))
                    contentType = MediaType.APPLICATION_JSON
                    accept = MediaType.APPLICATION_JSON
                }.andExpect {
                    status { isOk() }
                    content { json(mapper.writeValueAsString(ApiResponse.OK(expectedResponse))) }
                }.andDocument(
                    "blocked-member-find",
                    responseBody(
                        "[].id" responseType NUMBER means "유저 ID" example "1",
                        "[].email" responseType STRING means "유저 이메일" example "test@groubing.com",
                        "[].nickname" responseType STRING means "유저 닉네임" example "그루빙멤버",
                        "[].profileUrl" responseType STRING means "프로필 이미지 URL" isOptional true,
                    )
                )
            }
        }
    }
})
