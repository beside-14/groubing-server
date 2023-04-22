package com.beside.groubing.groubingserver.domain.bingo.api

import com.beside.groubing.groubingserver.config.ApiTest
import com.beside.groubing.groubingserver.docs.BOOLEAN
import com.beside.groubing.groubingserver.docs.NUMBER
import com.beside.groubing.groubingserver.docs.STRING
import com.beside.groubing.groubingserver.docs.andDocument
import com.beside.groubing.groubingserver.docs.requestBody
import com.beside.groubing.groubingserver.docs.requestType
import com.beside.groubing.groubingserver.docs.responseBody
import com.beside.groubing.groubingserver.docs.responseType
import com.beside.groubing.groubingserver.domain.bingo.application.BingoItemEditService
import com.beside.groubing.groubingserver.domain.bingo.payload.request.BingoItemEditRequest
import com.beside.groubing.groubingserver.domain.bingo.payload.response.BingoBoardResponse
import com.beside.groubing.groubingserver.extension.bingoBoard
import com.beside.groubing.groubingserver.extension.getHttpHeaderJwt
import com.beside.groubing.groubingserver.global.response.ApiResponse
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.single
import io.kotest.property.arbitrary.string
import io.mockk.every
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.patch

@ApiTest
@WebMvcTest(controllers = [BingoItemEditApi::class])
class BingoItemEditApiTest(
    private val mockMvc: MockMvc,
    private val mapper: ObjectMapper,
    @MockkBean private val bingoItemEditService: BingoItemEditService
) : BehaviorSpec({
    Given("빙고 아이템 수정 시") {
        val memberId = Arb.long(1L..100L).single()
        val bingoSize = Arb.int(3..4).single()
        val board = Arb.bingoBoard(memberId, bingoSize).single()

        When("필수 데이터가 유효하다면") {
            val request = BingoItemEditRequest(
                id = board.bingoItems[0].id,
                bingoBoardId = board.id,
                title = Arb.string(minSize = 2).single(),
                subTitle = null,
                imageUrl = null
            )
            val command = request.command(memberId)
            val item = board.findItem(request.id)
            val response = BingoBoardResponse.BingoItemResponse.fromBingoItem(item, command.memberId)

            Then("수정한 빙고 아이템을 리턴한다.") {
                every { bingoItemEditService.edit(any()) } returns response

                mockMvc.patch("/api/bingoboards/bingoitems") {
                    content = mapper.writeValueAsString(request)
                    contentType = MediaType.APPLICATION_JSON
                    header("Authorization", getHttpHeaderJwt(memberId))
                }.andExpect {
                    status { isOk() }
                    content { json(mapper.writeValueAsString(ApiResponse.OK(response))) }
                }.andDocument(
                    "edit-bingo-item",
                    requestBody(
                        "id" requestType NUMBER means "빙고 아이템 ID" example "1" isOptional false,
                        "bingoBoardId" requestType NUMBER means "빙고 ID" example "1" isOptional false,
                        "title" requestType STRING means "빙고 아이템 제목" example "이번 달은 갓생 살자." isOptional false,
                        "subTitle" requestType STRING means "빙고 아이템 내용" example "이번 달은 이것저것 다 해서 갓생 살자~!" isOptional true,
                        "imageUrl" requestType STRING means "빙고 아이템 이미지 URL" isOptional true
                    ),
                    responseBody(
                        "id" responseType NUMBER means "빙고 아이템 ID" example "1",
                        "title" responseType STRING means "빙고 아이템 제목" example "이번 달은 갓생 살자.",
                        "subTitle" responseType STRING means "빙고 아이템 내용" example "이번 달은 이것저것 다 해서 갓생 살자~!",
                        "imageUrl" responseType STRING means "빙고 아이템 이미지 URL",
                        "complete" responseType BOOLEAN means "false" example "`true` : 현재 유저는 해당 아이템 완료, `false` : 현재 유저는 해당 아이템 미완료",
                        "empty" responseType BOOLEAN means "TODO 작성 완료 여부" example "true"
                    )
                )
            }
        }
    }
})
