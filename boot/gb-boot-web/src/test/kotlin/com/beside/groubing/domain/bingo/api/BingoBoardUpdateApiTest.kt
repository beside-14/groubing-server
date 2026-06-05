package com.beside.groubing.domain.bingo.api

import com.beside.groubing.aEmptyBingo
import com.beside.groubing.config.ApiTest
import com.beside.groubing.docs.ARRAY
import com.beside.groubing.docs.BOOLEAN
import com.beside.groubing.docs.DATE
import com.beside.groubing.docs.NUMBER
import com.beside.groubing.docs.STRING
import com.beside.groubing.docs.andDocument
import com.beside.groubing.docs.requestBody
import com.beside.groubing.docs.requestType
import com.beside.groubing.docs.responseBody
import com.beside.groubing.domain.bingo.application.BingoBoardUpdateService
import com.beside.groubing.domain.bingo.payload.request.BingoBoardBaseUpdateRequest
import com.beside.groubing.domain.bingo.payload.request.BingoBoardMembersPeriodUpdateRequest
import com.beside.groubing.domain.bingo.payload.request.BingoBoardMemoUpdateRequest
import com.beside.groubing.domain.bingo.payload.request.BingoBoardOpenUpdateRequest
import com.beside.groubing.domain.bingo.payload.response.BingoBoardBaseUpdateResponse
import com.beside.groubing.domain.bingo.payload.response.BingoBoardMembersPeriodUpdateResponse
import com.beside.groubing.domain.bingo.payload.response.BingoBoardMemoUpdateResponse
import com.beside.groubing.domain.bingo.payload.response.BingoBoardOpenUpdateResponse
import com.beside.groubing.extension.encodedAs
import com.beside.groubing.extension.getHttpHeaderJwt
import com.beside.groubing.global.domain.id.ObfuscationType
import com.beside.groubing.global.response.ApiResponse
import com.beside.groubing.vocabulary.bingoBoardId
import com.beside.groubing.vocabulary.bingoGoal
import com.beside.groubing.vocabulary.bingoMembers
import com.beside.groubing.vocabulary.bingoMemo
import com.beside.groubing.vocabulary.bingoOpen
import com.beside.groubing.vocabulary.bingoSince
import com.beside.groubing.vocabulary.bingoTitle
import com.beside.groubing.vocabulary.bingoUntil
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.restdocs.snippet.Snippet
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.patch
import java.time.LocalDate

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
        val encodedId = id.encodedAs(ObfuscationType.BINGO_BOARD)
        val memberId = 1L

        val bingoBoardBaseUpdateRequest = BingoBoardBaseUpdateRequest(
            title = "New Title",
            goal = 5,
            since = LocalDate.now(),
            until = LocalDate.now().plusDays(7)
        )
        aEmptyBingo.updateBase(
            memberId, bingoBoardBaseUpdateRequest.title, bingoBoardBaseUpdateRequest.goal,
            bingoBoardBaseUpdateRequest.since, bingoBoardBaseUpdateRequest.until
        )
        every { bingoBoardUpdateService.updateBase(id, memberId, any()) } returns aEmptyBingo

        When("Base 정보 업데이트 시") {
            checkUpdateResponse(
                mockMvc,
                mapper,
                "/api/bingo-boards/{id}/base",
                encodedId,
                memberId,
                bingoBoardBaseUpdateRequest,
                ApiResponse.OK(BingoBoardBaseUpdateResponse.fromBingoBoard(aEmptyBingo)),
                "update-bingo-base",
                requestBody(
                    "title" requestType STRING means "빙고 제목" example "[테스트] 새로운 빙고입니다." formattedAs "^[a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣 -@\\[-_~]{1,40}",
                    "goal" requestType NUMBER means "달성 목표수, 빙고 사이즈가 3X3 인 경우 최대 3개, 4X4 인 경우 최대 4개" example "1",
                    "since" requestType DATE means "빙고 시작일자, 현재보다 미래로 설정" example "2023-01-01" formattedAs "yyyy-MM-dd",
                    "until" requestType DATE means "빙고 종료일자, 시작일자보다 미래로 설정" example "2023-02-01" formattedAs "yyyy-MM-dd"
                ),
                responseBody(
                    bingoBoardId(),
                    bingoTitle(),
                    bingoGoal(),
                    bingoSince(),
                    bingoUntil()
                )
            )
        }

        val bingoBoardMemoUpdateRequest = BingoBoardMemoUpdateRequest("빙고 메모입니다.")
        aEmptyBingo.updateBingoMemo(memberId, bingoBoardMemoUpdateRequest.memo)
        every { bingoBoardUpdateService.updateMemo(id, memberId, any()) } returns aEmptyBingo
        When("Memo 정보 업데이트 시") {
            checkUpdateResponse(
                mockMvc,
                mapper,
                "/api/bingo-boards/{id}/memo",
                encodedId,
                memberId,
                bingoBoardMemoUpdateRequest,
                ApiResponse.OK(BingoBoardMemoUpdateResponse.fromBingoBoard(aEmptyBingo)),
                "update-bingo-memo",
                requestBody(
                    "memo" requestType STRING means "빙고 메모" example "빙고 메모이며 `null` 일 수 있습니다."
                ),
                responseBody(
                    bingoBoardId(),
                    bingoMemo()
                )
            )
        }

        val bingoBoardOpenUpdateRequest = BingoBoardOpenUpdateRequest(false)
        aEmptyBingo.updateBingoOpen(memberId, bingoBoardOpenUpdateRequest.open)
        every { bingoBoardUpdateService.updateOpen(id, memberId, any()) } returns aEmptyBingo
        When("공개여부 정보 업데이트 시") {
            checkUpdateResponse(
                mockMvc,
                mapper,
                "/api/bingo-boards/{id}/open",
                encodedId,
                memberId,
                bingoBoardOpenUpdateRequest,
                ApiResponse.OK(BingoBoardOpenUpdateResponse.fromBingoBoard(aEmptyBingo)),
                "update-bingo-open",
                requestBody(
                    "open" requestType BOOLEAN means "피드 공개여부, `true` : 공개,`false` : 비공개" example "false"
                ),
                responseBody(
                    bingoBoardId(),
                    bingoOpen()
                )
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
        every {
            bingoBoardUpdateService.updateMembersPeriod(
                id,
                memberId,
                any()
            )
        } returns aEmptyBingo
        When("빙고멤버, 기간 업데이트 시") {
            checkUpdateResponse(
                mockMvc,
                mapper,
                "/api/bingo-boards/{id}/publish-info",
                encodedId,
                memberId,
                bingoBoardMembersPeriodUpdateRequest,
                ApiResponse.OK(BingoBoardMembersPeriodUpdateResponse.fromBingoBoard(aEmptyBingo)),
                "update-bingo-members-period",
                requestBody(
                    "bingoMembers" requestType ARRAY means "빙고 참여 멤버 리스트" example "2, 3, 7",
                    "since" requestType DATE means "빙고 시작 일자" example "2023-05-08",
                    "until" requestType DATE means "빙고 종료 일자" example "2023-05-15"
                ),
                responseBody(
                    bingoBoardId(),
                    bingoMembers(),
                    bingoSince(),
                    bingoUntil()
                )
            )
        }
    }
})

private fun checkUpdateResponse(
    mockMvc: MockMvc,
    mapper: ObjectMapper,
    url: String,
    bingoBoardId: String,
    memberId: Long,
    request: Any,
    expectedResponse: ApiResponse<*>,
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
