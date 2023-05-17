package com.beside.groubing.groubingserver.domain.bingo.api

import com.beside.groubing.groubingserver.aEnglishStudyBingoBoard
import com.beside.groubing.groubingserver.aMember
import com.beside.groubing.groubingserver.config.ApiTest
import com.beside.groubing.groubingserver.docs.ARRAY
import com.beside.groubing.groubingserver.docs.BOOLEAN
import com.beside.groubing.groubingserver.docs.ENUM
import com.beside.groubing.groubingserver.docs.NUMBER
import com.beside.groubing.groubingserver.docs.STRING
import com.beside.groubing.groubingserver.docs.andDocument
import com.beside.groubing.groubingserver.docs.optional
import com.beside.groubing.groubingserver.docs.pathVariables
import com.beside.groubing.groubingserver.docs.responseBody
import com.beside.groubing.groubingserver.docs.responseType
import com.beside.groubing.groubingserver.domain.bingo.application.BingoBoardFindService
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoardType
import com.beside.groubing.groubingserver.domain.bingo.domain.map.Direction
import com.beside.groubing.groubingserver.domain.bingo.payload.response.BingoBoardDetailResponse
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

@WebMvcTest(BingoBoardFindApi::class)
@ApiTest
class BingoBoardFindApiTest(
    private val mockMvc: MockMvc,
    @MockkBean private val bingoBoardFindService: BingoBoardFindService
) : BehaviorSpec({
    Given("BingoBoardFindApi가 주어졌을 때") {
        val memberId = 1L
        val bingoBoardId = 1L
        val bingoBoard = aEnglishStudyBingoBoard()
        val member = aMember(memberId)
        val otherMembers = (2L..5L).map { aMember(it) }

        val bingoBoardResponse = BingoBoardDetailResponse.fromBingoBoard(bingoBoard, member, otherMembers)

        every { bingoBoardFindService.findBingoBoard(memberId, bingoBoardId) } returns bingoBoardResponse

        When("GET /api/bingo-boards/{id} 요청이 들어왔을 때") {
            mockMvc.perform(
                get("/api/bingo-boards/{id}", bingoBoardId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", getHttpHeaderJwt(memberId))
            ).andDo(print())
                .andExpect(status().isOk)
                .andDocument(
                    "bingo-board-find",
                    pathVariables(
                        "id" optional true means "빙고 ID" example "1"
                    ),
                    responseBody(
                        "id" responseType NUMBER means "빙고 ID" example "1",
                        "title" responseType STRING means "빙고 제목" example "[테스트] 새로운 빙고입니다." formattedAs "^[a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣 -@\\[-_~]{1,40}",
                        "goal" responseType NUMBER means "달성 목표수, 빙고 사이즈가 3X3 인 경우 최대 3개, 4X4 인 경우 최대 4개" example "1",
                        "groupType" responseType ENUM(BingoBoardType::class) means "빙고 유형" example "`SINGLE`" formattedAs "개인 : `SINGLE`, 그룹 : `GROUP`",
                        "open" responseType BOOLEAN means "피드 공개여부, `true` : 공개,`false` : 비공개" example "false",
                        "dday" responseType STRING means "빙고 종료일자까지 남은 일 카운트",
                        "isStarted" responseType BOOLEAN means "빙고 보드의 임시저장 여부, 시작/종료일이 설정되지 않은 경우 임시저장으로 간주합니다." example "false" formattedAs "`true` : 임시저장 상태, `false` : 발행 상태",
                        "isFinished" responseType BOOLEAN means "빙고 보드의 진행 종료 여부, D-Day 기준으로 종료 여부를 확인합니다." example "false" formattedAs "`true` : 빙고 종료, `false` : 빙고 진행 중",
                        "bingoSize" responseType NUMBER means "빙고 사이즈" example "3" formattedAs "3X3 : 3, 4X4 : 4",
                        "memo" responseType STRING means "빙고 메모" example "빙고 메모이며 `null` 일 수 있습니다.",
                        "bingoMap.nickName" responseType STRING means "빙고 참여자 본인 닉네임" example "holeman79",
                        "bingoMap.bingoLines[].direction" responseType ENUM(Direction::class) means "빙고 축을 의미합니다." example "`HORIZONTAL`" formattedAs "X : `HORIZONTAL`, Y : `VERTICAL`, Z : `DIAGONAL`",
                        "bingoMap.bingoLines[].bingoItems[].id" responseType NUMBER means "빙고 아이템 ID" example "1",
                        "bingoMap.bingoLines[].bingoItems[].title" responseType STRING means "TODO" example "토익 만점 받기",
                        "bingoMap.bingoLines[].bingoItems[].subTitle" responseType STRING means "TODO 부가 설명, `null` 일 수 있습니다." example "토익 만점을 받으려면 열심히 공부해야 한다.",
                        "bingoMap.bingoLines[].bingoItems[].imageUrl" responseType STRING means "빙고 아이템 추가 이미지 URL, `null` 일 수 있습니다.",
                        "bingoMap.bingoLines[].bingoItems[].complete" responseType BOOLEAN means "TODO 달성 여부" example "true",
                        "bingoMap.bingoLines[].bingoItems[].itemOrder" responseType NUMBER means "빙고 아이템 순서" example "1, 2, 3...",
                        "bingoMap.totalCompleteCount" responseType NUMBER means "달성한 총 TODO 수",
                        "bingoMap.horizontalCompleteLineIndexes[]" responseType ARRAY means "X축 달성한 빙고 아이템 인덱스",
                        "bingoMap.verticalCompleteLineIndexes[]" responseType ARRAY means "Y축 달성한 빙고 아이템 인덱스",
                        "bingoMap.diagonalCompleteLineIndexes[]" responseType ARRAY means "Z축 달성한 빙고 아이템 인덱스",

                        "otherBingoMaps[].nickName" responseType STRING means "본인 외 빙고 참여자 닉네임" example "holeman79",
                        "otherBingoMaps[].bingoLines[].direction" responseType ENUM(Direction::class) means "빙고 축을 의미합니다." example "`HORIZONTAL`" formattedAs "X : `HORIZONTAL`, Y : `VERTICAL`, Z : `DIAGONAL`",
                        "otherBingoMaps[].bingoLines[].bingoItems[].id" responseType NUMBER means "빙고 아이템 ID" example "1",
                        "otherBingoMaps[].bingoLines[].bingoItems[].title" responseType STRING means "TODO" example "토익 만점 받기",
                        "otherBingoMaps[].bingoLines[].bingoItems[].subTitle" responseType STRING means "TODO 부가 설명, `null` 일 수 있습니다." example "토익 만점을 받으려면 열심히 공부해야 한다.",
                        "otherBingoMaps[].bingoLines[].bingoItems[].imageUrl" responseType STRING means "빙고 아이템 추가 이미지 URL, `null` 일 수 있습니다.",
                        "otherBingoMaps[].bingoLines[].bingoItems[].complete" responseType BOOLEAN means "TODO 달성 여부" example "true",
                        "otherBingoMaps[].bingoLines[].bingoItems[].itemOrder" responseType NUMBER means "빙고 아이템 순서" example "1, 2, 3...",
                        "otherBingoMaps[].totalCompleteCount" responseType NUMBER means "달성한 총 TODO 수",
                        "otherBingoMaps[].horizontalCompleteLineIndexes[]" responseType ARRAY means "X축 달성한 빙고 아이템 인덱스",
                        "otherBingoMaps[].verticalCompleteLineIndexes[]" responseType ARRAY means "Y축 달성한 빙고 아이템 인덱스",
                        "otherBingoMaps[].diagonalCompleteLineIndexes[]" responseType ARRAY means "Z축 달성한 빙고 아이템 인덱스",
                    )
                )
        }
    }
})
