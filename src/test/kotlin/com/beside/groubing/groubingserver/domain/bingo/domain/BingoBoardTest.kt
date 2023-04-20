package com.beside.groubing.groubingserver.domain.bingo.domain

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.boolean
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.single
import io.kotest.property.arbitrary.string

class BingoBoardTest : BehaviorSpec({
    Given("신규 보드 생성 시") {
        val memberId = Arb.long().single()
        val bingoSize = Arb.int(3..4).single()
        val board = BingoBoard.create(
            memberId = memberId,
            title = Arb.string().single(),
            goal = Arb.int(1..bingoSize).single(),
            boardType = Arb.enum<BingoBoardType>().single(),
            open = Arb.boolean().single(),
            bingoSize = bingoSize
        )

        When("설정한 빙고 사이즈만큼") {
            val items = board.bingoItems

            Then("빙고 아이템을 생성한다.") {
                items.size shouldBe (board.size * board.size)
            }
        }
    }
})
