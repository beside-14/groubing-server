package com.beside.groubing.domain.bingo.api

import com.beside.groubing.aTemporaryBingo
import com.beside.groubing.config.ApiTest
import com.beside.groubing.docs.BOOLEAN
import com.beside.groubing.docs.ENUM
import com.beside.groubing.docs.NUMBER
import com.beside.groubing.docs.STRING
import com.beside.groubing.docs.andDocument
import com.beside.groubing.docs.pathVariables
import com.beside.groubing.docs.requestParam
import com.beside.groubing.docs.responseBody
import com.beside.groubing.docs.responseType
import com.beside.groubing.domain.bingo.application.BingoItemShuffleService
import com.beside.groubing.domain.bingo.domain.map.Direction
import com.beside.groubing.extension.getHttpHeaderJwt
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
        val bingoMap = temporaryBingo.makeBingoMap(memberId)

        every { bingoItemShuffleService.shuffle(memberId = memberId, boardId = temporaryBingo.id) } returns bingoMap

        When("데이터가 유효하다면") {
            mockMvc.perform(
                RestDocumentationRequestBuilders.put(
                    "/api/bingo-boards/{id}/bingo-items",
                    temporaryBingo.id
                )
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", getHttpHeaderJwt(memberId))
            ).andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk)
                .andDocument(
                    "bingo-item-shuffle",
                    pathVariables(
                        "id" requestParam "빙고 ID" example "1" isOptional true
                    ),
                    responseBody(
                        "[].direction" responseType ENUM(Direction::class) means "빙고 축을 의미합니다." example "`HORIZONTAL`" formattedAs "X : `HORIZONTAL`, Y : `VERTICAL`, Z : `DIAGONAL`",
                        "[].bingoItems[].id" responseType NUMBER means "빙고 아이템 ID" example "1",
                        "[].bingoItems[].title" responseType STRING means "TODO" example "토익 만점 받기",
                        "[].bingoItems[].subTitle" responseType STRING means "TODO 부가 설명, `null` 일 수 있습니다." example "토익 만점을 받으려면 열심히 공부해야 한다.",
                        "[].bingoItems[].imageUrl" responseType STRING means "빙고 아이템 추가 이미지 URL, `null` 일 수 있습니다.",
                        "[].bingoItems[].complete" responseType BOOLEAN means "TODO 달성 여부" example "true",
                        "[].bingoItems[].itemOrder" responseType NUMBER means "빙고 아이템 순서" example "1, 2, 3...",
                        "[].bingoItems[].colorCode" responseType STRING means "빙고 아이템 Color Code" example "#F6A973"
                    )
                )
        }
    }
})
