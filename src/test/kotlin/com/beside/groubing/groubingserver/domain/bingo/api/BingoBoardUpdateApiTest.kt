package com.beside.groubing.groubingserver.domain.bingo.api

import com.beside.groubing.groubingserver.aEmptyBingo
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
import com.beside.groubing.groubingserver.domain.bingo.application.BingoBoardUpdateService
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoardType
import com.beside.groubing.groubingserver.domain.bingo.domain.map.Direction
import com.beside.groubing.groubingserver.domain.bingo.payload.request.BingoBoardBaseUpdateRequest
import com.beside.groubing.groubingserver.domain.bingo.payload.request.BingoBoardMembersPeriodUpdateRequest
import com.beside.groubing.groubingserver.domain.bingo.payload.request.BingoBoardMemoUpdateRequest
import com.beside.groubing.groubingserver.domain.bingo.payload.request.BingoBoardOpenUpdateRequest
import com.beside.groubing.groubingserver.domain.bingo.payload.response.BingoBoardResponse
import com.beside.groubing.groubingserver.extension.getHttpHeaderJwt
import com.beside.groubing.groubingserver.global.response.ApiResponse
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import java.time.LocalDate
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.restdocs.snippet.Snippet
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.patch

@ApiTest
@WebMvcTest(controllers = [BingoBoardUpdateApi::class])
class BingoBoardUpdateApiTest(
    private val mockMvc: MockMvc,
    private val mapper: ObjectMapper,
    @MockkBean private val bingoBoardUpdateService: BingoBoardUpdateService
) : BehaviorSpec({
    Given("빙고 업데이트 API가 주어졌을 때") {
        val aEmptyBingo = aEmptyBingo()
        val id = aEmptyBingo.id
        val memberId = 1L
        val responseSnippets = responseBody(
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
            "bingoLines[].bingoItems[].itemOrder" responseType NUMBER means "빙고 아이템 순서" example "1, 2, 3...",
            "totalCompleteCount" responseType NUMBER means "달성한 총 TODO 수",
            "horizontalCompleteLineIndexes[]" responseType ARRAY means "X축 달성한 빙고 아이템 인덱스",
            "verticalCompleteLineIndexes[]" responseType ARRAY means "Y축 달성한 빙고 아이템 인덱스",
            "diagonalCompleteLineIndexes[]" responseType ARRAY means "Z축 달성한 빙고 아이템 인덱스"
        )

        val bingoBoardBaseUpdateRequest = BingoBoardBaseUpdateRequest(
            title = "New Title",
            goal = 5,
            since = LocalDate.now(),
            until = LocalDate.now().plusDays(7)
        )
        aEmptyBingo.updateBase(memberId, bingoBoardBaseUpdateRequest.title, bingoBoardBaseUpdateRequest.goal,
            bingoBoardBaseUpdateRequest.since, bingoBoardBaseUpdateRequest.until)
        val baseUpdatedResponse = BingoBoardResponse.fromBingoBoard(aEmptyBingo, memberId)
        every { bingoBoardUpdateService.updateBase(id, memberId, any()) } returns baseUpdatedResponse

        When("Base 정보 업데이트 시") {
            checkUpdateResponse(
                mockMvc,
                mapper,
                "/api/bingoboards/{id}/base",
                id,
                memberId,
                bingoBoardBaseUpdateRequest,
                ApiResponse.OK(baseUpdatedResponse),
                "update-bingo-base",
                requestBody(
                    "title" requestType STRING means "빙고 제목" example "[테스트] 새로운 빙고입니다." formattedAs "^[a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣 -@\\[-_~]{1,40}",
                    "goal" requestType NUMBER means "달성 목표수, 빙고 사이즈가 3X3 인 경우 최대 3개, 4X4 인 경우 최대 4개" example "1",
                    "since" requestType DATE means "빙고 시작일자, 현재보다 미래로 설정" example "2023-01-01" formattedAs "yyyy-MM-dd",
                    "until" requestType DATE means "빙고 종료일자, 시작일자보다 미래로 설정" example "2023-02-01" formattedAs "yyyy-MM-dd"
                ),
                responseSnippets
            )
        }

        val bingoBoardMemoUpdateRequest = BingoBoardMemoUpdateRequest("빙고 메모입니다.")
        aEmptyBingo.updateBingoMemo(memberId, bingoBoardMemoUpdateRequest.memo)
        val memoUpdatedResponse = BingoBoardResponse.fromBingoBoard(aEmptyBingo, memberId)
        every { bingoBoardUpdateService.updateMemo(id, memberId, any()) } returns memoUpdatedResponse
        When("Memo 정보 업데이트 시") {
            checkUpdateResponse(
                mockMvc,
                mapper,
                "/api/bingoboards/{id}/memo",
                id,
                memberId,
                bingoBoardMemoUpdateRequest,
                ApiResponse.OK(memoUpdatedResponse),
                "update-bingo-memo",
                requestBody(
                    "memo" requestType STRING means "빙고 메모" example "빙고 메모이며 `null` 일 수 있습니다."
                ),
                responseSnippets
            )
        }

        val bingoBoardOpenUpdateRequest = BingoBoardOpenUpdateRequest(false)
        aEmptyBingo.updateBingoOpen(memberId, bingoBoardOpenUpdateRequest.open)
        val openUpdatedResponse = BingoBoardResponse.fromBingoBoard(aEmptyBingo, memberId)
        every { bingoBoardUpdateService.updateOpen(id, memberId, any()) } returns openUpdatedResponse
        When("공개여부 정보 업데이트 시") {
            checkUpdateResponse(
                mockMvc,
                mapper,
                "/api/bingoboards/{id}/open",
                id,
                memberId,
                bingoBoardOpenUpdateRequest,
                ApiResponse.OK(openUpdatedResponse),
                "update-bingo-open",
                requestBody(
                    "open" requestType BOOLEAN means "피드 공개여부, `true` : 공개,`false` : 비공개" example "false"
                ),
                responseSnippets
            )
        }

        val bingoBoardMembersPeriodUpdateRequest = BingoBoardMembersPeriodUpdateRequest(
            bingoMembers = listOf(2, 3, 7),
            since = LocalDate.now(),
            until = LocalDate.now().plusDays(7)
        )
        aEmptyBingo.updateBingoMembersPeriod(
            memberId,
            bingoBoardMembersPeriodUpdateRequest.bingoMembers,
            bingoBoardMembersPeriodUpdateRequest.since,
            bingoBoardMembersPeriodUpdateRequest.until
        )
        val bingoMembersPeriodUpdatedResponse = BingoBoardResponse.fromBingoBoard(aEmptyBingo, memberId)
        every { bingoBoardUpdateService.updateMembersPeriod(id, memberId, any()) } returns bingoMembersPeriodUpdatedResponse
        When("빙고멤버, 기간 업데이트 시") {
            checkUpdateResponse(
                mockMvc,
                mapper,
                "/api/bingoboards/{id}/members-period",
                id,
                memberId,
                bingoBoardMembersPeriodUpdateRequest,
                ApiResponse.OK(bingoMembersPeriodUpdatedResponse),
                "update-bingo-members-period",
                requestBody(
                    "bingoMembers" requestType ARRAY means "빙고 참여 멤버 리스트" example "2, 3, 7",
                    "since" requestType DATE means "빙고 시작 일자" example "2023-05-08",
                    "until" requestType DATE means "빙고 종료 일자" example "2023-05-15"
                ),
                responseSnippets
            )
        }
    }
})

private fun checkUpdateResponse(
    mockMvc: MockMvc,
    mapper: ObjectMapper,
    url: String,
    bingoBoardId: Long,
    memberId: Long,
    request: Any,
    expectedResponse: ApiResponse<BingoBoardResponse>,
    documentationIdentifier: String,
    requestFields: Snippet,
    responseFields: Snippet
) {
    mockMvc.patch(url, bingoBoardId) {
        content = mapper.writeValueAsString(request)
        contentType = MediaType.APPLICATION_JSON
        header("Authorization", getHttpHeaderJwt(memberId))
    }.andDo {
        print()
    }.andExpect {
        status { isOk() }
        content { json(mapper.writeValueAsString(expectedResponse)) }
    }.andDocument(
        documentationIdentifier,
        requestFields,
        responseFields
    )
}
