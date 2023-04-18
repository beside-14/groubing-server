package com.beside.groubing.groubingserver.domain.bingo.domain

import com.beside.groubing.groubingserver.extension.bingoBoard
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.property.Arb
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.single
import io.kotest.property.arbitrary.string

class BingoBoardTest : BehaviorSpec({
    Given("신규 보드 생성 시") {
        val memberId = Arb.long().single()
        val board = Arb.bingoBoard(memberId).single()

        When("설정한 빙고 사이즈만큼") {
            val items = board.bingoItems

            Then("빙고 아이템을 생성한다.") {
                items.size shouldBe (board.bingoSize * board.bingoSize)
            }
        }
    }

    Given("빙고 보드 수정 시") {
        val memberId = Arb.long().single()
        val board = Arb.bingoBoard(memberId).single()

        When("Null 이 아닌 데이터만") {
            val title = Arb.string().single()
            val goal = null
            board.editBoard(title, goal)

            Then("수정한다.") {
                board.title shouldBe title
                board.goal shouldNotBe goal
            }
        }
    }

    Given("빙고 아이템 수정 시") {
        val memberId = Arb.long().single()
        val board = Arb.bingoBoard(memberId).single()

        When("제목 및 Null 이 아닌 데이터만") {
            val bingoItemId = 0L
            val title = Arb.string().single()
            val subTitle = null
            val imageUrl = null
            board.editItem(bingoItemId, title, subTitle, imageUrl)

            Then("수정한다.") {
                val item = board.findItem(bingoItemId)
                item.title shouldBe title
            }
        }
    }
})
