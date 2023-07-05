package com.beside.groubing.groubingserver.domain.bingo.api

import com.beside.groubing.groubingserver.aEnglishStudyBingoBoard
import com.beside.groubing.groubingserver.config.ApiTest
import com.beside.groubing.groubingserver.docs.andDocument
import com.beside.groubing.groubingserver.docs.pathVariables
import com.beside.groubing.groubingserver.docs.requestParam
import com.beside.groubing.groubingserver.domain.bingo.application.BingoItemCompleteService
import com.beside.groubing.groubingserver.extension.getHttpHeaderJwt
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
        every { bingoItemCompleteService.completeBingoItem(englishBingoBoard.id, bingoItem.id, memberId) } returns Unit

        When("완료 요청 시") {
            mockMvc.perform(
                RestDocumentationRequestBuilders.patch(
                    "/api/bingo-boards/{id}/bingo-items/{bingoItemId}/complete",
                    englishBingoBoard.id,
                    bingoItem.id
                )
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", getHttpHeaderJwt(memberId))
            ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andDocument(
                    "bingo-item-complete",
                    pathVariables(
                        "id" requestParam "빙고 ID" example "1" isOptional true,
                        "bingoItemId" requestParam "빙고 아이템 ID" example "1" isOptional true
                    )
                )
        }

        every { bingoItemCompleteService.cancelBingoItem(englishBingoBoard.id, bingoItem.id, memberId) } returns Unit
        When("취소 요청 시") {
            mockMvc.perform(
                RestDocumentationRequestBuilders.patch(
                    "/api/bingo-boards/{id}/bingo-items/{bingoItemId}/cancel",
                    englishBingoBoard.id,
                    bingoItem.id
                )
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", getHttpHeaderJwt(memberId))
            ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andDocument(
                    "bingo-item-cancel",
                    pathVariables(
                        "id" requestParam "빙고 ID" example "1" isOptional true,
                        "bingoItemId" requestParam "빙고 아이템 ID" example "1" isOptional true
                    )
                )
        }
    }
})
