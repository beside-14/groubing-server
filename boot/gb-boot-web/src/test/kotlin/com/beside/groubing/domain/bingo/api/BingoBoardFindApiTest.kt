package com.beside.groubing.domain.bingo.api

import com.beside.groubing.aEnglishStudyBingoBoard
import com.beside.groubing.aMember
import com.beside.groubing.config.ApiTest
import com.beside.groubing.docs.andDocument
import com.beside.groubing.docs.pathVariables
import com.beside.groubing.docs.requestParam
import com.beside.groubing.docs.responseBody
import com.beside.groubing.domain.bingo.application.BingoBoardFindService
import com.beside.groubing.domain.bingo.domain.BingoBoardDetail
import com.beside.groubing.extension.encodedAs
import com.beside.groubing.extension.getHttpHeaderJwt
import com.beside.groubing.global.domain.id.ObfuscationType
import com.beside.groubing.vocabulary.bingoBoardId
import com.beside.groubing.vocabulary.bingoBoardIdPath
import com.beside.groubing.vocabulary.bingoBoardType
import com.beside.groubing.docs.NUMBER
import com.beside.groubing.docs.responseType
import com.beside.groubing.vocabulary.bingoCompleted
import com.beside.groubing.vocabulary.bingoFinished
import com.beside.groubing.vocabulary.bingoGoal
import com.beside.groubing.vocabulary.bingoIsLeader
import com.beside.groubing.vocabulary.bingoItemColorCode
import com.beside.groubing.vocabulary.bingoItemComplete
import com.beside.groubing.vocabulary.bingoItemId
import com.beside.groubing.vocabulary.bingoItemImageUrl
import com.beside.groubing.vocabulary.bingoItemOrder
import com.beside.groubing.vocabulary.bingoItemSubTitle
import com.beside.groubing.vocabulary.bingoItemTitle
import com.beside.groubing.vocabulary.bingoLineDirection
import com.beside.groubing.vocabulary.bingoMapNickName
import com.beside.groubing.vocabulary.bingoMemo
import com.beside.groubing.vocabulary.bingoOpen
import com.beside.groubing.vocabulary.bingoSince
import com.beside.groubing.vocabulary.bingoSize
import com.beside.groubing.vocabulary.bingoTitle
import com.beside.groubing.vocabulary.bingoUntil
import com.beside.groubing.vocabulary.diagonalBingoIndexes
import com.beside.groubing.vocabulary.horizontalBingoIndexes
import com.beside.groubing.vocabulary.totalBingoCount
import com.beside.groubing.vocabulary.verticalBingoIndexes
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
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
        val authentication: Authentication = SecurityContextHolder.getContext().authentication

        val memberId = authentication.principal as Long
        val encodedMemberId = memberId.encodedAs(ObfuscationType.MEMBER)
        val bingoBoardId = 1L
        val encodedBingoBoardId = bingoBoardId.encodedAs(ObfuscationType.BINGO_BOARD)
        val bingoBoard = aEnglishStudyBingoBoard()
        val member = aMember(memberId).toDomain()
        val otherMembers = (2L..5L).map { aMember(it).toDomain() }

        every { bingoBoardFindService.findOne(memberId, bingoBoardId) } returns BingoBoardDetail(bingoBoard, member, otherMembers)

        When("GET /api/bingo-boards/{id} 요청이 들어왔을 때") {
            mockMvc.perform(
                get("/api/bingo-boards/{id}", encodedBingoBoardId)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", getHttpHeaderJwt(memberId))
                    .param("memberId", encodedMemberId)
            ).andDo(print())
                .andExpect(status().isOk)
                .andDocument(
                    "bingo-board-find",
                    pathVariables(
                        bingoBoardIdPath() example encodedBingoBoardId isOptional true
                    ),
                    requestParam(
                        "memberId" requestParam "회원 ID (obfuscated)" example encodedMemberId
                    ),
                    responseBody(
                        bingoBoardId(),
                        bingoTitle(),
                        bingoGoal(),
                        bingoBoardType("groupType"),
                        bingoOpen(),
                        "dday" responseType NUMBER means "빙고 종료일자까지 남은 일 카운트",
                        bingoMemo(),
                        bingoIsLeader(),
                        bingoSince(),
                        bingoUntil(),
                        bingoCompleted(),
                        bingoFinished(),
                        bingoSize(),
                        bingoMapNickName("bingoMap.nickName", "빙고 참여자 본인 닉네임"),
                        bingoLineDirection("bingoMap.bingoLines[].direction"),
                        bingoItemId("bingoMap.bingoLines[].bingoItems[].id"),
                        bingoItemTitle("bingoMap.bingoLines[].bingoItems[].title"),
                        bingoItemSubTitle("bingoMap.bingoLines[].bingoItems[].subTitle"),
                        bingoItemImageUrl("bingoMap.bingoLines[].bingoItems[].imageUrl"),
                        bingoItemComplete("bingoMap.bingoLines[].bingoItems[].complete"),
                        bingoItemOrder("bingoMap.bingoLines[].bingoItems[].itemOrder"),
                        bingoItemColorCode("bingoMap.bingoLines[].bingoItems[].colorCode"),
                        totalBingoCount("bingoMap.totalBingoCount"),
                        horizontalBingoIndexes("bingoMap.horizontalBingoIndexes[]"),
                        verticalBingoIndexes("bingoMap.verticalBingoIndexes[]"),
                        diagonalBingoIndexes("bingoMap.diagonalBingoIndexes[]"),

                        bingoMapNickName("otherBingoMaps[].nickName", "본인 외 빙고 참여자 닉네임"),
                        bingoLineDirection("otherBingoMaps[].bingoLines[].direction"),
                        bingoItemId("otherBingoMaps[].bingoLines[].bingoItems[].id"),
                        bingoItemTitle("otherBingoMaps[].bingoLines[].bingoItems[].title"),
                        bingoItemSubTitle("otherBingoMaps[].bingoLines[].bingoItems[].subTitle"),
                        bingoItemImageUrl("otherBingoMaps[].bingoLines[].bingoItems[].imageUrl"),
                        bingoItemComplete("otherBingoMaps[].bingoLines[].bingoItems[].complete"),
                        bingoItemOrder("otherBingoMaps[].bingoLines[].bingoItems[].itemOrder"),
                        bingoItemColorCode("otherBingoMaps[].bingoLines[].bingoItems[].colorCode"),
                        totalBingoCount("otherBingoMaps[].totalBingoCount"),
                        horizontalBingoIndexes("otherBingoMaps[].horizontalBingoIndexes[]"),
                        verticalBingoIndexes("otherBingoMaps[].verticalBingoIndexes[]"),
                        diagonalBingoIndexes("otherBingoMaps[].diagonalBingoIndexes[]"),
                    )
                )
        }
    }
})
