package com.beside.groubing.groubingserver.domain.bingo.api

import com.beside.groubing.groubingserver.config.ApiTest
import com.beside.groubing.groubingserver.docs.ARRAY
import com.beside.groubing.groubingserver.docs.BOOLEAN
import com.beside.groubing.groubingserver.docs.ENUM
import com.beside.groubing.groubingserver.docs.NUMBER
import com.beside.groubing.groubingserver.docs.STRING
import com.beside.groubing.groubingserver.docs.andDocument
import com.beside.groubing.groubingserver.docs.requestBody
import com.beside.groubing.groubingserver.docs.requestType
import com.beside.groubing.groubingserver.docs.responseBody
import com.beside.groubing.groubingserver.docs.responseType
import com.beside.groubing.groubingserver.domain.bingo.application.BingoBoardEditService
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoardType
import com.beside.groubing.groubingserver.domain.bingo.domain.map.Direction
import com.beside.groubing.groubingserver.domain.bingo.payload.request.BingoBoardEditRequest
import com.beside.groubing.groubingserver.domain.bingo.payload.request.BingoBoardMemoEditRequest
import com.beside.groubing.groubingserver.domain.bingo.payload.request.BingoBoardOpenableEditRequest
import com.beside.groubing.groubingserver.domain.bingo.payload.response.BingoBoardResponse
import com.beside.groubing.groubingserver.extension.bingoBoard
import com.beside.groubing.groubingserver.extension.getHttpHeaderJwt
import com.beside.groubing.groubingserver.global.response.ApiResponse
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.single
import io.kotest.property.arbitrary.string
import io.kotest.property.arbitrary.stringPattern
import io.mockk.every
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.patch

@ApiTest
@WebMvcTest(controllers = [BingoBoardEditApi::class])
class BingoBoardEditApiTest(
    private val mockMvc: MockMvc,
    private val mapper: ObjectMapper,
    @MockkBean private val bingoBoardEditService: BingoBoardEditService
) : BehaviorSpec({
    Given("빙고 수정 요청 시") {
        val memberId = Arb.long(1L..100L).single()
        val pattern = "^[a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣 -@\\[-_~]{1,40}"
        val bingoSize = Arb.int(3..4).single()
        val board = Arb.bingoBoard(memberId, bingoSize).single()

        When("제목 혹은 목표수 데이터가 유효하다면") {
            val request = BingoBoardEditRequest(
                id = board.id,
                title = Arb.stringPattern(pattern).single(),
                goal = Arb.int(1..bingoSize).single()
            )
            board.editBoard(request.title, request.goal)
            val response = BingoBoardResponse.fromBingoBoard(board, memberId)

            Then("수정한 빙고를 리턴한다.") {
                every { bingoBoardEditService.editBoard(any()) } returns response

                mockMvc.patch("/api/bingos") {
                    content = mapper.writeValueAsString(request)
                    contentType = MediaType.APPLICATION_JSON
                    header("Authorization", getHttpHeaderJwt(memberId))
                }.andExpect {
                    status { isOk() }
                    content { json(mapper.writeValueAsString(ApiResponse.OK(response))) }
                }.andDocument(
                    "edit-bingo-board",
                    requestBody(
                        "id" requestType NUMBER means "빙고 ID" example "1" isOptional false,
                        "title" requestType STRING means "빙고 제목" example "[테스트] 새로운 빙고입니다." formattedAs pattern isOptional true,
                        "goal" requestType NUMBER means "달성 목표수, 빙고 사이즈가 3X3 인 경우 최대 3개, 4X4 인 경우 최대 4개" example "1" isOptional true
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
                        "bingoLines[].direction" responseType ENUM(Direction::class) means "빙고 축을 의미합니다." example "`HORIZONTAL`" formattedAs "X : `HORIZONTAL`, Y : `VERTICAL`, Z : `DIAGONAL`",
                        "bingoLines[].bingoItems[].id" responseType NUMBER means "빙고 아이템 ID" example "1",
                        "bingoLines[].bingoItems[].title" responseType STRING means "TODO" example "토익 만점 받기",
                        "bingoLines[].bingoItems[].subTitle" responseType STRING means "TODO 부가 설명, `null` 일 수 있습니다." example "토익 만점을 받으려면 열심히 공부해야 한다.",
                        "bingoLines[].bingoItems[].imageUrl" responseType STRING means "빙고 아이템 추가 이미지 URL, `null` 일 수 있습니다.",
                        "bingoLines[].bingoItems[].complete" responseType BOOLEAN means "TODO 달성 여부" example "true",
                        "bingoLines[].bingoItems[].empty" responseType BOOLEAN means "TODO 작성 완료 여부" example "true",
                        "totalCompleteCount" responseType NUMBER means "달성한 총 TODO 수",
                        "horizontalCompleteLineIndexes[]" responseType ARRAY means "X축 달성한 빙고 아이템 인덱스",
                        "verticalCompleteLineIndexes[]" responseType ARRAY means "Y축 달성한 빙고 아이템 인덱스",
                        "diagonalCompleteLineIndexes[]" responseType ARRAY means "Z축 달성한 빙고 아이템 인덱스"
                    )
                )
            }
        }

        When("메모 데이터가 유효하다면") {
            val request = BingoBoardMemoEditRequest(
                id = board.id,
                memo = Arb.string().single()
            )
            board.editMemo(request.memo)
            val response = BingoBoardResponse.fromBingoBoard(board, memberId)

            Then("수정한 빙고를 리턴한다.") {
                every { bingoBoardEditService.editMemo(any(), any(), any()) } returns response

                mockMvc.patch("/api/bingos/memo") {
                    content = mapper.writeValueAsString(request)
                    contentType = MediaType.APPLICATION_JSON
                    header("Authorization", getHttpHeaderJwt(memberId))
                }.andExpect {
                    status { isOk() }
                    content { json(mapper.writeValueAsString(ApiResponse.OK(response))) }
                }.andDocument(
                    "edit-bingo-board-memo",
                    requestBody(
                        "id" requestType NUMBER means "빙고 ID" example "1" isOptional false,
                        "memo" requestType STRING means "빙고 메모" example "이번 주까지 파티 준비 마무리하기" formattedAs "`null` 허용" isOptional false
                    )
                )
            }
        }

        When("공개여부 데이터가 유효하다면") {
            val request = BingoBoardOpenableEditRequest(
                id = board.id,
                open = !board.open
            )
            board.editOpenable(request.open)
            val response = BingoBoardResponse.fromBingoBoard(board, memberId)

            Then("수정한 빙고를 리턴한다.") {
                every { bingoBoardEditService.editOpenable(any(), any(), any()) } returns response

                mockMvc.patch("/api/bingos/openable") {
                    content = mapper.writeValueAsString(request)
                    contentType = MediaType.APPLICATION_JSON
                    header("Authorization", getHttpHeaderJwt(memberId))
                }.andExpect {
                    status { isOk() }
                    content { json(mapper.writeValueAsString(ApiResponse.OK(response))) }
                }.andDocument(
                    "edit-bingo-board-open",
                    requestBody(
                        "id" requestType NUMBER means "빙고 ID" example "1" isOptional false,
                        "open" requestType BOOLEAN means "피드 공개여부, `true` : 공개,`false` : 비공개" example "false"
                    )
                )
            }
        }
    }
})
