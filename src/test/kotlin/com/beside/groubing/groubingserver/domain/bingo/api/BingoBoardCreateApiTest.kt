package com.beside.groubing.groubingserver.domain.bingo.api

import com.beside.groubing.groubingserver.config.ApiTest
import com.beside.groubing.groubingserver.docs.ARRAY
import com.beside.groubing.groubingserver.docs.BOOLEAN
import com.beside.groubing.groubingserver.docs.DATE
import com.beside.groubing.groubingserver.docs.ENUM
import com.beside.groubing.groubingserver.docs.NUMBER
import com.beside.groubing.groubingserver.docs.STRING
import com.beside.groubing.groubingserver.docs.andDocument
import com.beside.groubing.groubingserver.docs.requestBody
import com.beside.groubing.groubingserver.docs.requestType
import com.beside.groubing.groubingserver.docs.responseBody
import com.beside.groubing.groubingserver.docs.responseType
import com.beside.groubing.groubingserver.domain.bingo.application.BingoBoardCreateService
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoardType
import com.beside.groubing.groubingserver.domain.bingo.domain.map.Direction
import com.beside.groubing.groubingserver.domain.bingo.payload.request.BingoBoardCreateRequest
import com.beside.groubing.groubingserver.domain.bingo.payload.response.BingoBoardResponse
import com.beside.groubing.groubingserver.extension.getHttpHeaderJwt
import com.beside.groubing.groubingserver.global.response.ApiResponse
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.single
import io.kotest.property.arbitrary.stringPattern
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
        val memberId = Arb.long(1L..100L).single()
        val bingoSize = Arb.int(3..4).single()
        val pattern = "^[a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣 -@\\[-_~]{1,40}"
        val request = BingoBoardCreateRequest(
            title = Arb.stringPattern(pattern).single(),
            goal = Arb.int(1..3).single(),
            boardType = Arb.enum<BingoBoardType>().single(),
            open = Arb.boolean().single(),
            bingoSize = bingoSize
        )

        When("데이터가 유효하다면") {
            val command = request.command(memberId)
            val board = command.toNewBingoBoard()
            val response = BingoBoardResponse.fromBingoBoard(board, memberId)

            Then("생성된 빙고를 리턴한다.") {
                every { bingoBoardCreateService.create(any()) } returns response

                mockMvc.post("/api/bingos") {
                    content = mapper.writeValueAsString(request)
                    contentType = MediaType.APPLICATION_JSON
                    header("Authorization", getHttpHeaderJwt())
                }.andDo {
                    print()
                }.andExpect {
                    status { isOk() }
                    content { json(mapper.writeValueAsString(ApiResponse.OK(response))) }
                }.andDocument(
                    "create-bingo",
                    requestBody(
                        "title" requestType STRING means "빙고 제목" example "[테스트] 새로운 빙고입니다." formattedAs pattern,
                        "goal" requestType NUMBER means "달성 목표수, 빙고 사이즈가 3X3 인 경우 최대 3개, 4X4 인 경우 최대 4개" example "1",
                        "boardType" requestType ENUM(BingoBoardType::class) means "빙고 유형" example "`SINGLE`" formattedAs "개인 : `SINGLE`, 그룹 : `GROUP`",
                        "open" requestType BOOLEAN means "피드 공개여부, `true` : 공개,`false` : 비공개" example "false",
                        "since" requestType DATE means "빙고 시작일자, 현재보다 미래로 설정" example "2023-01-01" formattedAs "yyyy-MM-dd",
                        "until" requestType DATE means "빙고 종료일자, 시작일자보다 미래로 설정" example "2023-02-01" formattedAs "yyyy-MM-dd",
                        "bingoSize" requestType NUMBER means "빙고 사이즈" example "3" formattedAs "3X3 : 3, 4X4 : 4"
                    ),
                    responseBody(
                        "id" responseType NUMBER means "빙고 ID" example "1",
                        "title" responseType STRING means "빙고 제목" example "[테스트] 새로운 빙고입니다." formattedAs "^[a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣 -@\\[-_~]{1,40}",
                        "goal" responseType NUMBER means "달성 목표수, 빙고 사이즈가 3X3 인 경우 최대 3개, 4X4 인 경우 최대 4개" example "1",
                        "groupType" responseType ENUM(BingoBoardType::class) means "빙고 유형" example "`SINGLE`" formattedAs "개인 : `SINGLE`, 그룹 : `GROUP`",
                        "open" responseType BOOLEAN means "피드 공개여부, `true` : 공개,`false` : 비공개" example "false",
                        "dday" responseType STRING means "빙고 종료일자까지 남은 일 카운트",
                        "bingoSize" responseType NUMBER means "빙고 사이즈" example "3" formattedAs "3X3 : 3, 4X4 : 4",
                        "memo" responseType STRING means "빙고 메모" example "빙고 메모이며 `null` 일 수 있습니다.",
                        "bingoLines[].direction" responseType ENUM(Direction::class) means "빙고 축을 의미합니다." example "`HORIZONTAL`" formattedAs "X : `HORIZONTAL`, Y : `VERTICAL`, Z : `DIAGONAL`",
                        "bingoLines[].bingoItems[].id" responseType NUMBER means "빙고 아이템 ID" example "1",
                        "bingoLines[].bingoItems[].title" responseType STRING means "TODO" example "토익 만점 받기",
                        "bingoLines[].bingoItems[].subTitle" responseType STRING means "TODO 부가 설명, `null` 일 수 있습니다." example "토익 만점을 받으려면 열심히 공부해야 한다.",
                        "bingoLines[].bingoItems[].imageUrl" responseType STRING means "빙고 아이템 추가 이미지 URL, `null` 일 수 있습니다.",
                        "bingoLines[].bingoItems[].complete" responseType BOOLEAN means "TODO 달성 여부" example "true",
                        "totalCompleteCount" responseType NUMBER means "달성한 총 TODO 수",
                        "horizontalCompleteLineIndexes[]" responseType ARRAY means "X축 달성한 빙고 아이템 인덱스",
                        "verticalCompleteLineIndexes[]" responseType ARRAY means "Y축 달성한 빙고 아이템 인덱스",
                        "diagonalCompleteLineIndexes[]" responseType ARRAY means "Z축 달성한 빙고 아이템 인덱스"
                    )
                )
            }
        }
    }
})
