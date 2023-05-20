package com.beside.groubing.groubingserver.domain.bingo.api

import com.beside.groubing.groubingserver.aEmptyBingo
import com.beside.groubing.groubingserver.bingoBoardResponseSnippets
import com.beside.groubing.groubingserver.config.ApiTest
import com.beside.groubing.groubingserver.docs.BOOLEAN
import com.beside.groubing.groubingserver.docs.DATE
import com.beside.groubing.groubingserver.docs.ENUM
import com.beside.groubing.groubingserver.docs.NUMBER
import com.beside.groubing.groubingserver.docs.STRING
import com.beside.groubing.groubingserver.docs.andDocument
import com.beside.groubing.groubingserver.docs.requestBody
import com.beside.groubing.groubingserver.docs.requestType
import com.beside.groubing.groubingserver.domain.bingo.application.BingoBoardCreateService
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoardType
import com.beside.groubing.groubingserver.domain.bingo.payload.request.BingoBoardCreateRequest
import com.beside.groubing.groubingserver.domain.bingo.payload.response.BingoBoardResponse
import com.beside.groubing.groubingserver.extension.getHttpHeaderJwt
import com.beside.groubing.groubingserver.global.response.ApiResponse
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@ApiTest
@WebMvcTest(controllers = [BingoBoardCreateApi::class])
class BingoBoardCreateApiTest(
    private val mockMvc: MockMvc,
    private val mapper: ObjectMapper,
    @MockkBean private val bingoBoardCreateService: BingoBoardCreateService
) : BehaviorSpec({
    Given("신규 빙고 생성 요청 시") {
        val aEmptyBingo = aEmptyBingo()
        val memberId = 1L
        val request = BingoBoardCreateRequest(
            title = aEmptyBingo.title,
            goal = aEmptyBingo.goal,
            boardType = aEmptyBingo.boardType,
            open = aEmptyBingo.open,
            bingoSize = aEmptyBingo.size
        )
        val response = BingoBoardResponse.fromBingoBoard(aEmptyBingo, memberId)
        every { bingoBoardCreateService.create(any()) } returns response

        When("데이터가 유효하다면") {
            mockMvc.post("/api/bingo-boards") {
                content = mapper.writeValueAsString(request)
                contentType = MediaType.APPLICATION_JSON
                header("Authorization", getHttpHeaderJwt(memberId))
            }.andDo {
                print()
            }.andExpect {
                status { isOk() }
                content { json(mapper.writeValueAsString(ApiResponse.OK(response))) }
            }.andDocument(
                "create-bingo",
                requestBody(
                    "title" requestType STRING means "빙고 제목" example "[테스트] 새로운 빙고입니다." formattedAs "^[a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣 -@\\[-_~]{1,40}",
                    "goal" requestType NUMBER means "달성 목표수, 빙고 사이즈가 3X3 인 경우 최대 3개, 4X4 인 경우 최대 4개" example "1",
                    "boardType" requestType ENUM(BingoBoardType::class) means "빙고 유형" example "`SINGLE`" formattedAs "개인 : `SINGLE`, 그룹 : `GROUP`",
                    "open" requestType BOOLEAN means "피드 공개여부, `true` : 공개,`false` : 비공개" example "false",
                    "since" requestType DATE means "빙고 시작일자, 현재보다 미래로 설정" example "2023-01-01" formattedAs "yyyy-MM-dd",
                    "until" requestType DATE means "빙고 종료일자, 시작일자보다 미래로 설정" example "2023-02-01" formattedAs "yyyy-MM-dd",
                    "bingoSize" requestType NUMBER means "빙고 사이즈" example "3" formattedAs "3X3 : 3, 4X4 : 4"
                ),
                bingoBoardResponseSnippets
            )
        }
    }
})
