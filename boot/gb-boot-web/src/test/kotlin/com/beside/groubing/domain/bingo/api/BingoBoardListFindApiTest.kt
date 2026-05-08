package com.beside.groubing.domain.bingo.api

import com.beside.groubing.aEnglishStudyBingoBoard
import com.beside.groubing.aGameBingoBoard
import com.beside.groubing.aHealthBingoBoard
import com.beside.groubing.config.ApiTest
import com.beside.groubing.docs.andDocument
import com.beside.groubing.docs.requestParam
import com.beside.groubing.docs.responseBody
import com.beside.groubing.domain.bingo.application.BingoBoardListFindService
import com.beside.groubing.extension.getHttpHeaderJwt
import com.beside.groubing.vocabulary.bingoBoardId
import com.beside.groubing.vocabulary.bingoBoardType
import com.beside.groubing.vocabulary.bingoColorValue
import com.beside.groubing.vocabulary.bingoCompleted
import com.beside.groubing.vocabulary.bingoFinished
import com.beside.groubing.vocabulary.bingoGoal
import com.beside.groubing.vocabulary.bingoIsLeader
import com.beside.groubing.vocabulary.bingoItemComplete
import com.beside.groubing.vocabulary.bingoItemOrder
import com.beside.groubing.vocabulary.bingoLineDirection
import com.beside.groubing.vocabulary.bingoOpen
import com.beside.groubing.vocabulary.bingoSince
import com.beside.groubing.vocabulary.bingoTitle
import com.beside.groubing.vocabulary.bingoUntil
import com.beside.groubing.vocabulary.totalBingoCount
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
        val loginMemberId = 2L

        val bingoBoards = listOf(
            aEnglishStudyBingoBoard(),
            aHealthBingoBoard(),
            aGameBingoBoard()
        )

        every {
            bingoBoardListFindService.find(
                memberId,
                loginMemberId
            )
        } returns bingoBoards

        When("GET /api/bingo-boards 요청이 들어왔을 때") {
            mockMvc.perform(
                get("/api/bingo-boards")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .param("memberId", "1")
                    .header("Authorization", getHttpHeaderJwt(loginMemberId))
            ).andDo(print())
                .andExpect(status().isOk)
                .andDocument(
                    "bingo-board-list-find",
                    requestParam(
                        "memberId" requestParam "멤버 ID" example "1" isOptional true
                    ),
                    responseBody(
                        bingoBoardId("[].id"),
                        bingoTitle("[].title"),
                        bingoSince("[].since"),
                        bingoUntil("[].until"),
                        bingoGoal("[].goal"),
                        bingoBoardType("[].groupType"),
                        bingoColorValue("[].bingoColorValue"),
                        bingoOpen("[].open"),
                        bingoIsLeader("[].isLeader"),
                        bingoCompleted("[].completed"),
                        bingoFinished("[].finished"),
                        bingoLineDirection("[].bingoLines[].direction"),
                        bingoItemComplete("[].bingoLines[].bingoItems[].complete"),
                        bingoItemOrder("[].bingoLines[].bingoItems[].itemOrder"),
                        totalBingoCount("[].totalBingoCount")
                    )
                )
        }
    }
})
