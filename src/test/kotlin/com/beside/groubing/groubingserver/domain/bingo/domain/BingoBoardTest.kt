package com.beside.groubing.groubingserver.domain.bingo.domain

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
import java.time.LocalDate

class BingoBoardTest : BehaviorSpec({
    Given("신규 보드 생성 시") {
        val now = LocalDate.now()
        val tomorrow = now.plusDays(1)
        val board = BingoBoard(
            title = Arb.string().single(),
            type = Arb.enum<BingoType>().single(),
            size = Arb.enum<BingoSize>().single(),
            color = Arb.enum<BingoColor>().single(),
            goal = Arb.int(1..1).single(),
            open = Arb.boolean().single(),
            since = Arb.localDate(minDate = now, maxDate = tomorrow).single(),
            until = Arb.localDate(minDate = tomorrow.plusDays(1)).single(),
            creatorId = Arb.long(0L..100L).single()
        )

        When("설정한 빙고 사이즈만큼") {
            val items = board.getItems()

            Then("빙고 아이템을 생성한다.") {
                items.size shouldBe board.size.x
                items.forEachIndexed { x, bingoItems ->
                    bingoItems.forEachIndexed { y, bingoItem ->
                        bingoItem.positionX shouldBe x
                        bingoItem.positionY shouldBe y
                    }
                }
            }
        }

        And("기존 빙고 보드에") {
            val otherBoard = BingoBoard(
                title = Arb.string().single(),
                type = Arb.enum<BingoType>().single(),
                size = Arb.enum<BingoSize>().single(),
                color = Arb.enum<BingoColor>().single(),
                goal = Arb.int(1..1).single(),
                open = Arb.boolean().single(),
                since = Arb.localDate(minDate = now, maxDate = tomorrow).single(),
                until = Arb.localDate(minDate = tomorrow.plusDays(1)).single(),
                creatorId = Arb.long(0L..100L).single()
            )
            val otherItems = otherBoard.getItems()

            When("다른 보드의 아이템으로") {
                board.setItems(otherItems)

                Then("변경한다.") {
                    val items = board.getItems()

                    otherItems.forEachIndexed { x, bingoItems ->
                        bingoItems.forEachIndexed { y, bingoItem -> items[x][y] shouldBe bingoItem }
                    }
                }
            }
        }
    }
})
