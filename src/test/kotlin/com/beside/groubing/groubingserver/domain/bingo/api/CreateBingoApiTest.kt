package com.beside.groubing.groubingserver.domain.bingo.api

import com.beside.groubing.groubingserver.config.WithAuthMember
import com.beside.groubing.groubingserver.docs.BOOLEAN
import com.beside.groubing.groubingserver.docs.DATE
import com.beside.groubing.groubingserver.docs.ENUM
import com.beside.groubing.groubingserver.docs.NUMBER
import com.beside.groubing.groubingserver.docs.STRING
import com.beside.groubing.groubingserver.docs.andDocument
import com.beside.groubing.groubingserver.docs.requestBody
import com.beside.groubing.groubingserver.docs.requestType
import com.beside.groubing.groubingserver.docs.responseBody
import com.beside.groubing.groubingserver.docs.responseType
import com.beside.groubing.groubingserver.domain.bingo.application.CreateBingoService
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoard
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoColor
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoSize
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoType
import com.beside.groubing.groubingserver.domain.bingo.payload.request.CreateBingoRequest
import com.beside.groubing.groubingserver.domain.bingo.payload.response.BingoResponse
import com.beside.groubing.groubingserver.domain.member.domain.Member
import com.beside.groubing.groubingserver.global.response.ApiResponse
import com.fasterxml.jackson.databind.ObjectMapper
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.localDate
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.single
import io.kotest.property.arbitrary.stringPattern
import io.mockk.every
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import java.time.LocalDate

@WithAuthMember
@WebMvcTest(controllers = [CreateBingoApi::class])
@AutoConfigureMockMvc(addFilters = false)
@AutoConfigureRestDocs
class CreateBingoApiTest(
    private val mockMvc: MockMvc,
    private val mapper: ObjectMapper,
    @MockkBean private val createBingoService: CreateBingoService
) : BehaviorSpec({
    Given("신규 빙고 생성 요청 시") {
        val now = LocalDate.now()
        val tomorrow = now.plusDays(1)
        val memberId = Arb.long(1L..100L).single()
        val pattern = "^[a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣 -@\\[-_~]{1,40}"
        val request = CreateBingoRequest(
            title = Arb.stringPattern(pattern).single(),
            type = Arb.enum<BingoType>().single(),
            size = Arb.enum<BingoSize>().single(),
            color = Arb.enum<BingoColor>().single(),
            goal = Arb.int(1..3).single(),
            open = Arb.boolean().single(),
            since = Arb.localDate(minDate = now, maxDate = tomorrow).single(),
            until = Arb.localDate(minDate = tomorrow.plusDays(1)).single()
        )

        When("데이터가 유효하다면") {
            val board = BingoBoard(
                title = request.title,
                type = request.type,
                size = request.size,
                color = request.color,
                goal = request.goal,
                open = request.open,
                since = request.since,
                until = request.until,
                member = Member(id = memberId)
            )
            val items = board.createNewItems()
            val response = ApiResponse.OK(BingoResponse(board, items))

            Then("생성된 빙고를 리턴한다.") {
                every { createBingoService.create(any()) } returns response.data

                mockMvc.post("/api/bingos") {
                    content = mapper.writeValueAsString(request)
                    contentType = MediaType.APPLICATION_JSON
                }.andDo {
                    print()
                }.andExpect {
                    status { isOk() }
                    content { json(mapper.writeValueAsString(response)) }
                }.andDocument(
                    "create-bingo",
                    requestBody(
                        "title" requestType STRING means "빙고 제목" example "[테스트] 새로운 빙고입니다." formattedAs pattern,
                        "type" requestType ENUM(BingoType::class) means "빙고 유형" example "`PERSONAL`" formattedAs "개인 : `PERSONAL`, 그룹 : `GROUP`",
                        "size" requestType ENUM(BingoSize::class) means "빙고 사이즈" example "`NINE`" formattedAs "3x3 : `NINE`, 4x4 : `SIXTEEN`",
                        "color" requestType ENUM(BingoColor::class) means "빙고 색상" example "`RANDOM`" formattedAs "`RED`, `ORANGE`, `YELLOW`, `GREEN`, `BLUE`, `NAVY`, `PURPLE`, `RANDOM`",
                        "goal" requestType NUMBER means "달성 목표수, 빙고 사이즈가 3x3 인 경우 최대 3개, 4x4 인 경우 최대 4개" example "1",
                        "open" requestType BOOLEAN means "피드 공개여부, `true` : 공개,`false` : 비공개" example "false",
                        "since" requestType DATE means "빙고 시작일자, 현재보다 미래로 설정" example "2023-01-01" formattedAs "yyyy-MM-dd",
                        "until" requestType DATE means "빙고 종료일자, 시작일자보다 미래로 설정" example "2023-02-01" formattedAs "yyyy-MM-dd"
                    ),
                    responseBody(
                        "bingoBoardId" responseType NUMBER means "빙고 ID" example "1",
                        "creator.id" responseType NUMBER means "작성자 ID" example "1",
                        "creator.email" responseType STRING means "작성자 이메일" example "test@groubing.com",
                        "title" responseType STRING means "빙고 제목" example "[테스트] 새로운 빙고입니다." formattedAs "^[a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣 -@\\[-_~]{1,40}",
                        "type" responseType ENUM(BingoType::class) means "빙고 유형" example "`PERSONAL`" formattedAs "개인 : `PERSONAL`, 그룹 : `GROUP`",
                        "size.name" responseType STRING means "빙고 사이즈명" example "NINE" formattedAs "3x3 : `NINE`, 4x4 : `SIXTEEN`",
                        "size.x" responseType NUMBER means "빙고 X축 크기" example "3" formattedAs "`NINE` : 3, `SIXTEEN` : 4",
                        "size.y" responseType NUMBER means "빙고 Y축 크기" example "3" formattedAs "`NINE` : 3, `SIXTEEN` : 4",
                        "size.description" responseType STRING means "빙고 사이즈 설명" example "3x3" formattedAs "`NINE` : `3x3`, `SIXTEEN` : `4x4`",
                        "color.name" responseType STRING means "빙고 색상명" example "RANDOM" formattedAs "`RED`, `ORANGE`, `YELLOW`, `GREEN`, `BLUE`, `NAVY`, `PURPLE`, `RANDOM`",
                        "color.hex" responseType STRING means "빙고 색상 HEX 코드" example "#ff0000",
                        "color.description" responseType STRING means "빙고 색상 설명" example "빨강색",
                        "goal" responseType NUMBER means "달성 목표수, 빙고 사이즈가 3x3 인 경우 최대 3개, 4x4 인 경우 최대 4개" example "1",
                        "open" responseType BOOLEAN means "피드 공개여부, `true` : 공개,`false` : 비공개" example "false",
                        "since" responseType DATE means "빙고 시작일자, 현재보다 미래로 설정" example "2023-01-01" formattedAs "yyyy-MM-dd",
                        "until" responseType DATE means "빙고 종료일자, 시작일자보다 미래로 설정" example "2023-02-01" formattedAs "yyyy-MM-dd",
                        "memo" responseType STRING means "빙고 비고 메모" example "빙고 메모 입니다.",
                        "items[].bingoBoardId" responseType NUMBER means "빙고 ID" example "1",
                        "items[].bingoItemId" responseType NUMBER means "빙고 아이템 ID" example "1",
                        "items[].title" responseType STRING means "할 일" example "토익 점수 990점 달성",
                        "items[].subtitle" responseType STRING means "부가설명" example "LC 집중적으로 학습하기",
                        "items[].imageUrl" responseType STRING means "이미지 URL",
                        "items[].positionX" responseType NUMBER means "빙고 아이템 X축 좌표" example "0",
                        "items[].positionY" responseType NUMBER means "빙고 아이템 Y축 좌표" example "0"
                    )
                )
            }
        }
    }
})
