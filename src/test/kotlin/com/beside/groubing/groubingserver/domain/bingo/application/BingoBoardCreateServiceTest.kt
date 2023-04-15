package com.beside.groubing.groubingserver.domain.bingo.application

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoardType
import com.beside.groubing.groubingserver.domain.bingo.payload.command.BingoBoardCreateCommand
import com.beside.groubing.groubingserver.domain.bingo.payload.response.BingoBoardResponse
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.nulls.beNull
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.localDate
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.single
import io.kotest.property.arbitrary.string
import io.mockk.every
import io.mockk.verify
import java.time.LocalDate

class BingoBoardCreateServiceTest(
    @MockkBean private val bingoBoardCreateService: BingoBoardCreateService
) : BehaviorSpec({

    Given("신규 빙고 생성 시") {
        val now = LocalDate.now()
        val tomorrow = now.plusDays(1)
        val memberId = Arb.long().single()
        val bingoSize = Arb.int(3..4).single()
        val command = BingoBoardCreateCommand.createBingoBoardCommand(
            memberId = memberId,
            title = Arb.string().single(),
            goal = Arb.int(1..bingoSize).single(),
            boardType = Arb.enum<BingoBoardType>().single(),
            open = Arb.boolean().single(),
            since = Arb.localDate(minDate = now, maxDate = tomorrow).single(),
            until = Arb.localDate(minDate = tomorrow).single(),
            bingoSize = bingoSize
        )

        When("빙고 보드 및 아이템을 생성하고") {
            val board = command.toNewBingoBoard()

            Then("해당 데이터를 리턴한다.") {
                every { bingoBoardCreateService.create(any()) } returns BingoBoardResponse.fromBingoBoard(
                    board,
                    memberId
                )

                val response = bingoBoardCreateService.create(command)

                response.dDay shouldBe "D-${board.calculateLeftDays()}"
                response.memo shouldBe beNull()
                response.bingoLines.size shouldBe bingoSize

                verify(exactly = 1) { bingoBoardCreateService.create(any()) }
            }
        }
    }
})
