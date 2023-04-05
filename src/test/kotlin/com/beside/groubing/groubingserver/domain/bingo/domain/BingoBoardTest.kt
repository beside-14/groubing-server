package com.beside.groubing.groubingserver.domain.bingo.domain

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.inspectors.forExactly
import io.kotest.matchers.shouldBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.enum
import io.kotest.property.arbitrary.single

class BingoBoardTest : BehaviorSpec({
    Given("신규 보드 생성 시") {
        val board = BingoBoard(type = Arb.enum<BingoType>().single())

        When("설정한 빙고 사이즈만큼") {
            val items = board.createNewItems()

            Then("빙고 아이템을 생성한다.") {
                items.size shouldBe board.size.x
                items.forExactly(board.size.x) { it.board.id shouldBe board.id }
            }
        }
    }
})
