package com.beside.groubing.groubingserver.domain.bingo.application

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoard
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoBoardMapper
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoColor
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoSize
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoType
import com.beside.groubing.groubingserver.domain.bingo.payload.command.CreateBingoCommand
import com.beside.groubing.groubingserver.domain.bingo.payload.response.BingoResponse
import com.beside.groubing.groubingserver.domain.member.domain.Member
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.inspectors.forExactly
import io.kotest.matchers.date.shouldBeBefore
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.beBlank
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.localDate
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.single
import io.kotest.property.arbitrary.stringPattern
import io.mockk.every
import io.mockk.verify
import org.springframework.context.annotation.Import
import java.time.LocalDate

@Import(BingoBoardMapper::class)
class CreateBingoServiceTest(
    @MockkBean private val createBingoService: CreateBingoService
) : BehaviorSpec({

    Given("신규 빙고 생성 시") {
        val now = LocalDate.now()
        val command = CreateBingoCommand(
            memberId = Arb.long(0L..100L).single(),
            title = Arb.stringPattern("").single(),
            type = Arb.enum<BingoType>().single(),
            size = Arb.enum<BingoSize>().single(),
            color = Arb.enum<BingoColor>().single(),
            goal = Arb.int(1..3).single(),
            open = Arb.boolean().single(),
            since = Arb.localDate(maxDate = now).single(),
            until = Arb.localDate(minDate = now.plusDays(1)).single()
        )

        When("빙고 보드 및 아이템을 생성하고") {
            val board =
                BingoBoard(
                    title = command.title,
                    type = command.type,
                    size = command.size,
                    color = command.color,
                    goal = command.goal,
                    open = command.open,
                    since = command.since,
                    until = command.until,
                    member = Member(id = command.memberId)
                )
            val items = board.createNewItems()

            Then("해당 데이터를 리턴한다.") {
                every { createBingoService.create(any()) } returns BingoResponse(board, items)

                val response = createBingoService.create(command)

                response.since shouldBeBefore response.until
                response.memo shouldBe beBlank()
                response.items.size shouldBe command.size.x
                response.items.forExactly(command.size.x) { it.bingoBoardId shouldBe response.bingoBoardId }

                verify(exactly = 1) { createBingoService.create(any()) }
            }
        }
    }
})
