package com.beside.groubing.domain.bingo.api

import com.beside.groubing.aEnglishStudyBingoBoard
import com.beside.groubing.config.ApiTest
import com.beside.groubing.docs.andDocument
import com.beside.groubing.docs.pathVariables
import com.beside.groubing.docs.requestParam
import com.beside.groubing.docs.responseBody
import com.beside.groubing.domain.bingo.application.BingoItemCompleteService
import com.beside.groubing.extension.encodedAs
import com.beside.groubing.extension.getHttpHeaderJwt
import com.beside.groubing.global.domain.id.ObfuscationType
import com.beside.groubing.vocabulary.bingoBoardIdPath
import com.beside.groubing.vocabulary.diagonalBingoIndexes
import com.beside.groubing.vocabulary.horizontalBingoIndexes
import com.beside.groubing.vocabulary.totalBingoCount
import com.beside.groubing.vocabulary.verticalBingoIndexes
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers

@ApiTest
@WebMvcTest(controllers = [BingoItemCompleteApi::class])
class BingoItemCompleteApiTest(
    private val mockMvc: MockMvc,
    @MockkBean private val bingoItemCompleteService: BingoItemCompleteService
) : BehaviorSpec({
    Given("빙고 아이템 상태 업데이트 요청 시") {
        val englishBingoBoard = aEnglishStudyBingoBoard()
        val memberId = 1L
        val bingoItem = englishBingoBoard.bingoItems[0]
        val encodedBingoBoardId = englishBingoBoard.id.encodedAs(ObfuscationType.BINGO_BOARD)
        val encodedBingoItemId = bingoItem.id.encodedAs(ObfuscationType.BINGO_ITEM)
        val bingoMap = englishBingoBoard.makeBingoMap(memberId)
        every { bingoItemCompleteService.complete(englishBingoBoard.id, bingoItem.id, memberId) } returns bingoMap

        val responseBody = responseBody(
            horizontalBingoIndexes("horizontalBingoIndexes[]"),
            verticalBingoIndexes("verticalBingoIndexes[]"),
            diagonalBingoIndexes("diagonalBingoIndexes[]"),
            totalBingoCount("totalBingoCount")
        )

        When("완료 요청 시") {
            mockMvc.perform(
                RestDocumentationRequestBuilders.patch(
                    "/api/bingo-boards/{bingoBoardId}/bingo-items/{bingoItemId}/complete",
                    encodedBingoBoardId,
                    encodedBingoItemId
                )
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", getHttpHeaderJwt(memberId))
            ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andDocument(
                    "bingo-item-complete",
                    pathVariables(
                        bingoBoardIdPath() example encodedBingoBoardId isOptional true,
                        "bingoItemId" requestParam "빙고 아이템 ID (obfuscated)" example encodedBingoItemId isOptional true
                    ),
                    responseBody
                )
        }

        every { bingoItemCompleteService.cancel(englishBingoBoard.id, bingoItem.id, memberId) } returns bingoMap
        When("취소 요청 시") {
            mockMvc.perform(
                RestDocumentationRequestBuilders.patch(
                    "/api/bingo-boards/{bingoBoardId}/bingo-items/{bingoItemId}/cancel",
                    encodedBingoBoardId,
                    encodedBingoItemId
                )
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", getHttpHeaderJwt(memberId))
            ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andDocument(
                    "bingo-item-cancel",
                    pathVariables(
                        bingoBoardIdPath() example encodedBingoBoardId isOptional true,
                        "bingoItemId" requestParam "빙고 아이템 ID (obfuscated)" example encodedBingoItemId isOptional true
                    ),
                    responseBody
                )
        }
    }
})
