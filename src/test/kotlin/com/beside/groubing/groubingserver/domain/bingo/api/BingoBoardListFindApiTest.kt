package com.beside.groubing.groubingserver.domain.bingo.api

import com.beside.groubing.groubingserver.aEnglishStudyBingoBoard
import com.beside.groubing.groubingserver.aGameBingoBoard
import com.beside.groubing.groubingserver.aHealthBingoBoard
import com.beside.groubing.groubingserver.config.ApiTest
import com.beside.groubing.groubingserver.docs.BOOLEAN
import com.beside.groubing.groubingserver.docs.DATE
import com.beside.groubing.groubingserver.docs.ENUM
import com.beside.groubing.groubingserver.docs.NUMBER
import com.beside.groubing.groubingserver.docs.STRING
import com.beside.groubing.groubingserver.docs.andDocument
import com.beside.groubing.groubingserver.docs.requestParam
import com.beside.groubing.groubingserver.docs.responseBody
import com.beside.groubing.groubingserver.docs.responseType
import com.beside.groubing.groubingserver.domain.bingo.application.BingoBoardListFindService
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoardType
import com.beside.groubing.groubingserver.domain.bingo.domain.map.Direction
import com.beside.groubing.groubingserver.domain.bingo.payload.response.BingoBoardOverviewResponse
import com.beside.groubing.groubingserver.extension.getHttpHeaderJwt
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers.print
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(BingoBoardListFindApi::class)
@ApiTest
class BingoBoardListFindApiTest(
    private val mockMvc: MockMvc,
    @MockkBean private val bingoBoardListFindService: BingoBoardListFindService
) : BehaviorSpec({
    Given("BingoBoardListFindApi가 주어졌을 때") {
        val memberId = 1L

        val bingoBoardOverviewResponses = listOf(
            BingoBoardOverviewResponse.fromBingoBoard(aEnglishStudyBingoBoard(), memberId),
            BingoBoardOverviewResponse.fromBingoBoard(aHealthBingoBoard(), memberId),
            BingoBoardOverviewResponse.fromBingoBoard(aGameBingoBoard(), memberId)
        )

        every { bingoBoardListFindService.findBingoBoardList(memberId) } returns bingoBoardOverviewResponses

        When("GET /api/bingo-boards 요청이 들어왔을 때") {
            mockMvc.perform(
                get("/api/bingo-boards")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .param("memberId", "1")
                    .header("Authorization", getHttpHeaderJwt(memberId))
            ).andDo(print())
                .andExpect(status().isOk)
                .andDocument(
                    "bingo-board-list-find",
                    requestParam(
                        "memberId" requestParam "멤버 ID" example "1" isOptional true
                    ),
                    responseBody(
                        "[].id" responseType NUMBER means "빙고 ID" example "1",
                        "[].title" responseType STRING means "빙고 제목" example "[테스트] 새로운 빙고입니다." formattedAs "^[a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣 -@\\[-_~]{1,40}",
                        "[].since" responseType DATE means "빙고 시작일자, 현재보다 미래로 설정" example "2023-01-01" formattedAs "yyyy-MM-dd",
                        "[].until" responseType DATE means "빙고 종료일자, 시작일자보다 미래로 설정" example "2023-02-01" formattedAs "yyyy-MM-dd",
                        "[].goal" responseType NUMBER means "달성 목표수, 빙고 사이즈가 3X3 인 경우 최대 3개, 4X4 인 경우 최대 4개" example "1",
                        "[].groupType" responseType ENUM(BingoBoardType::class) means "빙고 유형" example "`SINGLE`" formattedAs "개인 : `SINGLE`, 그룹 : `GROUP`",
                        "[].bingoColorValue" responseType STRING means "빙고 Color" example "#B8B7FC" formattedAs "Blue : `#8BC0FC`, Orange: `#FCB179`, Red: `FF8282`, Green: `#55DEB5`, Purple : `#B8B7FC`",
                        "[].open" responseType BOOLEAN means "피드 공개여부, `true` : 공개,`false` : 비공개" example "false",
                        "[].isLeader" responseType BOOLEAN means "해당 빙고 리더 여부 `true` : 리더,`false` : 멤버" example "true",
                        "[].completed" responseType BOOLEAN means "완성된 빙고 여부, `true` : 완성 빙고,`false` : 임시 빙고" example "false",
                        "[].finished" responseType BOOLEAN means "빙고 기간 종료 여부, `true` : 종료,`false` : 진행 중" example "false",
                        "[].bingoLines[].direction" responseType ENUM(Direction::class) means "빙고 축을 의미합니다." example "`HORIZONTAL`" formattedAs "X : `HORIZONTAL`, Y : `VERTICAL`, Z : `DIAGONAL`",
                        "[].bingoLines[].bingoItems[].complete" responseType BOOLEAN means "TODO 달성 여부" example "true",
                        "[].bingoLines[].bingoItems[].itemOrder" responseType NUMBER means "빙고 아이템 순서" example "1, 2, 3...",
                        "[].totalBingoCount" responseType NUMBER means "달성한 총 빙고 수"
                    )
                )
        }
    }
})
