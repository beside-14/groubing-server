package com.beside.groubing.domain.bingo.api

import com.beside.groubing.aTemporaryBingo
import com.beside.groubing.config.ApiTest
import com.beside.groubing.docs.andDocument
import com.beside.groubing.docs.pathVariables
import com.beside.groubing.docs.responseBody
import com.beside.groubing.domain.bingo.application.BingoItemShuffleService
import com.beside.groubing.extension.encodedAs
import com.beside.groubing.extension.getHttpHeaderJwt
import com.beside.groubing.global.domain.id.ObfuscationType
import com.beside.groubing.vocabulary.bingoBoardIdPath
import com.beside.groubing.vocabulary.bingoItemColorCode
import com.beside.groubing.vocabulary.bingoItemComplete
import com.beside.groubing.vocabulary.bingoItemId
import com.beside.groubing.vocabulary.bingoItemImageUrl
import com.beside.groubing.vocabulary.bingoItemOrder
import com.beside.groubing.vocabulary.bingoItemSubTitle
import com.beside.groubing.vocabulary.bingoItemTitle
import com.beside.groubing.vocabulary.bingoLineDirection
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
@WebMvcTest(controllers = [BingoItemShuffleApi::class])
class BingoItemShuffleApiTest(
    private val mockMvc: MockMvc,
    @MockkBean private val bingoItemShuffleService: BingoItemShuffleService
) : BehaviorSpec({
    Given("빙고 아이템 섞기 요청 시") {
        val memberId = 1L

        val temporaryBingo = aTemporaryBingo()
        temporaryBingo.shuffleBingoItems()
        val encodedBingoBoardId = temporaryBingo.id.encodedAs(ObfuscationType.BINGO_BOARD)
        val bingoMap = temporaryBingo.makeBingoMap(memberId)

        every { bingoItemShuffleService.shuffle(memberId = memberId, boardId = temporaryBingo.id) } returns bingoMap

        When("데이터가 유효하다면") {
            mockMvc.perform(
                RestDocumentationRequestBuilders.put(
                    "/api/bingo-boards/{bingoBoardId}/bingo-items",
                    encodedBingoBoardId
                )
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", getHttpHeaderJwt(memberId))
            ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andDocument(
                    "bingo-item-shuffle",
                    pathVariables(
                        bingoBoardIdPath() example encodedBingoBoardId isOptional true
                    ),
                    responseBody(
                        bingoLineDirection("[].direction"),
                        bingoItemId("[].bingoItems[].id"),
                        bingoItemTitle("[].bingoItems[].title"),
                        bingoItemSubTitle("[].bingoItems[].subTitle"),
                        bingoItemImageUrl("[].bingoItems[].imageUrl"),
                        bingoItemComplete("[].bingoItems[].complete"),
                        bingoItemOrder("[].bingoItems[].itemOrder"),
                        bingoItemColorCode("[].bingoItems[].colorCode")
                    )
                )
        }
    }
})
