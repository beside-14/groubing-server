package com.beside.groubing.groubingserver.domain.bingo.api

import com.beside.groubing.groubingserver.aEmptyBingo
import com.beside.groubing.groubingserver.config.ApiTest
import com.beside.groubing.groubingserver.docs.BOOLEAN
import com.beside.groubing.groubingserver.docs.NUMBER
import com.beside.groubing.groubingserver.docs.STRING
import com.beside.groubing.groubingserver.docs.andDocument
import com.beside.groubing.groubingserver.docs.optional
import com.beside.groubing.groubingserver.docs.pathVariables
import com.beside.groubing.groubingserver.docs.requestBody
import com.beside.groubing.groubingserver.docs.requestType
import com.beside.groubing.groubingserver.docs.responseBody
import com.beside.groubing.groubingserver.docs.responseType
import com.beside.groubing.groubingserver.domain.bingo.application.BingoItemUpdateService
import com.beside.groubing.groubingserver.domain.bingo.payload.request.BingoItemUpdateRequest
import com.beside.groubing.groubingserver.domain.bingo.payload.response.BingoItemResponse
import com.beside.groubing.groubingserver.extension.getHttpHeaderJwt
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
        val response = BingoItemResponse.fromBingoItem(bingoItem, memberId)
        every { bingoItemUpdateService.updateBingoItem(aEmptyBingo.id, bingoItem.id, memberId, any()) } returns response

        When("데이터가 유효하다면") {
            mockMvc.perform(
                RestDocumentationRequestBuilders.put("/api/bingo-boards/{id}/bingo-items/{bingoItemId}", aEmptyBingo.id, bingoItem.id)
                    .content(mapper.writeValueAsString(request))
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", getHttpHeaderJwt(memberId))
            ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andDocument(
                    "bingo-item-update",
                    pathVariables(
                        "id" optional true means "빙고 ID" example "1",
                        "bingoItemId" optional true means "빙고 아이템 ID" example "1"
                    ),
                    requestBody(
                        "title" requestType STRING means "빙고 아이템 제목" example request.title,
                        "subTitle" requestType STRING means "빙고 아이템 제목 상세" example "8월까지 끝내기"
                    ),
                    responseBody(
                        "id" responseType NUMBER means "빙고 아이템 ID" example "1",
                        "title" responseType STRING means "빙고 아이템 제목" example request.title,
                        "subTitle" responseType STRING means "빙고 아이템 제목 상세" example "8월까지 끝내기",
                        "imageUrl" responseType STRING means "빙고 아이템 이미지 Url" example "http://10.0.40.246/item1.svg",
                        "itemOrder" responseType NUMBER means "빙고 아이템 순서" example "1, 2, 3",
                        "complete" responseType BOOLEAN means "빙고 아이템 완료 여부" example "true"
                    )
                )
        }
    }
})
