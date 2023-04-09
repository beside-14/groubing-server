package com.beside.groubing.groubingserver.domain.bingo.domain

import io.kotest.core.spec.style.BehaviorSpec
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
                items.forEachIndexed { x, bingoItems ->
                    bingoItems.forEachIndexed { y, bingoItem ->
                        bingoItem.positionX shouldBe x
                        bingoItem.positionY shouldBe y
                    }
                }
            }
        }

        And("기존 빙고 보드에") {
            val otherBoard = BingoBoard(type = board.type, size = board.size)
            val otherItems = otherBoard.createNewItems()

            When("다른 보드의 아이템으로") {
                board.setItems(otherItems)

                Then("변경한다.") {
                    val items = board.getItems()

                    otherItems.forEachIndexed { x, bingoItems ->
                        bingoItems.forEachIndexed { y, bingoItem -> items[x][y] shouldBe bingoItem }
                    }
                }
            }

            When("기존 빙고 아이템을") {
                val newItems = board.createNewItems()
                val items = board.getItems()

                Then("가져온다.") {
                    items.size shouldBe newItems.size
                    items.forEachIndexed { x, bingoItems ->
                        bingoItems.forEachIndexed { y, bingoItem -> bingoItem shouldBe newItems[x][y] }
                    }
                }
            }
        }
    }
})
