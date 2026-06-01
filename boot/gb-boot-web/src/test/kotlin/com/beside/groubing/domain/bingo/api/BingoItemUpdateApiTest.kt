package com.beside.groubing.domain.bingo.api

import com.beside.groubing.aEmptyBingo
import com.beside.groubing.config.ApiTest
import com.beside.groubing.docs.STRING
import com.beside.groubing.docs.andDocument
import com.beside.groubing.docs.pathVariables
import com.beside.groubing.docs.requestBody
import com.beside.groubing.docs.requestParam
import com.beside.groubing.docs.requestType
import com.beside.groubing.docs.responseBody
import com.beside.groubing.domain.bingo.application.BingoItemUpdateService
import com.beside.groubing.domain.bingo.payload.request.BingoItemUpdateRequest
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
import com.fasterxml.jackson.databind.ObjectMapper
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
@WebMvcTest(controllers = [BingoItemUpdateApi::class])
class BingoItemUpdateApiTest(
    private val mockMvc: MockMvc,
    private val mapper: ObjectMapper,
    @MockkBean private val bingoItemUpdateService: BingoItemUpdateService
) : BehaviorSpec({
    Given("빙고 아이템 업데이트 요청 시") {
        val aEmptyBingo = aEmptyBingo()
        val memberId = 1L
        val request = BingoItemUpdateRequest(
            title = "영어 회화 마스터",
            subTitle = "8월까지 끝내기"
        )
        val bingoItem = aEmptyBingo.bingoItems[0]
        val encodedBingoBoardId = aEmptyBingo.id.encodedAs(ObfuscationType.BINGO_BOARD)
        val encodedBingoItemId = bingoItem.id.encodedAs(ObfuscationType.BINGO_ITEM)
        every { bingoItemUpdateService.update(aEmptyBingo.id, bingoItem.id, memberId, any()) } returns bingoItem

        When("데이터가 유효하다면") {
            mockMvc.perform(
                RestDocumentationRequestBuilders.put(
                    "/api/bingo-boards/{bingoBoardId}/bingo-items/{bingoItemId}",
                    encodedBingoBoardId,
                    encodedBingoItemId
                )
                    .content(mapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", getHttpHeaderJwt(memberId))
            ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andDocument(
                    "bingo-item-update",
                    pathVariables(
                        bingoBoardIdPath() example encodedBingoBoardId isOptional true,
                        "bingoItemId" requestParam "빙고 아이템 ID (obfuscated)" example encodedBingoItemId isOptional true
                    ),
                    requestBody(
                        "title" requestType STRING means "빙고 아이템 제목" example request.title,
                        "subTitle" requestType STRING means "빙고 아이템 제목 상세" example "8월까지 끝내기"
                    ),
                    responseBody(
                        bingoItemId("id"),
                        bingoItemTitle("title") means "빙고 아이템 제목" example request.title,
                        bingoItemSubTitle("subTitle") means "빙고 아이템 제목 상세" example "8월까지 끝내기",
                        bingoItemImageUrl("imageUrl") means "빙고 아이템 이미지 Url" example "http://10.0.40.246/item1.svg",
                        bingoItemOrder("itemOrder") example "1, 2, 3",
                        bingoItemComplete("complete") means "빙고 아이템 완료 여부",
                        bingoItemColorCode("colorCode")
                    )
                )
        }
    }
})
