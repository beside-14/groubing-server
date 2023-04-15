package com.beside.groubing.groubingserver.domain.bingo.application

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoard
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoardType
import com.beside.groubing.groubingserver.domain.bingo.payload.command.BingoBoardEditCommand
import com.beside.groubing.groubingserver.domain.bingo.payload.response.BingoBoardResponse
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
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
import java.time.LocalDate

class BingoBoardEditServiceTest(
    @MockkBean private val bingoBoardEditService: BingoBoardEditService
) : BehaviorSpec({
    Given("빙고 수정 시") {
        val now = LocalDate.now()
        val tomorrow = now.plusDays(1)
        val memberId = Arb.long().single()
        val bingoSize = Arb.int(3..4).single()
        val command = BingoBoardEditCommand.createCommand(
            memberId = memberId,
            id = Arb.long().single(),
            title = Arb.string().single(),
            goal = Arb.int(1..bingoSize).single(),
            since = Arb.localDate(minDate = now, maxDate = tomorrow).single(),
            until = Arb.localDate(minDate = tomorrow).single(),
            memo = Arb.string().single()
        )

        When("수정 권한 확인 후") {
            val board = BingoBoard
                .createBingoBoard(
                    memberId = command.memberId,
                    title = command.title!!,
                    goal = command.goal!!,
                    boardType = Arb.enum<BingoBoardType>().single(),
                    open = Arb.boolean().single(),
                    since = command.since!!,
                    until = command.until!!,
                    bingoSize = bingoSize
                )

            Then("요청한 데이터로 수정한다.") {
                every { bingoBoardEditService.edit(any()) } returns BingoBoardResponse.fromBingoBoard(board, memberId)

                val response = bingoBoardEditService.edit(command)

                response.title shouldBe command.title
                response.goal shouldBe command.goal
                response.dDay shouldBe "D-${board.calculateLeftDays()}"
            }
        }
    }
})
