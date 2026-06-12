package com.beside.groubing.domain.bingo.domain

import com.beside.groubing.domain.bingo.exception.BingoIllegalStateException
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class BingoItemsTest : BehaviorSpec({

    fun items(size: Int = 3, updated: Boolean = true): BingoItems {
        val total = size * size
        return BingoItems.of(
            (1..total).map { order ->
                BingoItem.of(
                    id = order.toLong(),
                    title = if (updated) "item-$order" else null,
                    subTitle = null,
                    imageUrl = "g",
                    itemOrder = order,
                    colorCode = "#2787C9",
                    completeMembers = mutableSetOf()
                )
            }
        )
    }

    Given("BingoItems.create") {
        When("boardSize 와 알파벳 목록으로 생성하면") {
            val alphabets = listOf("g", "r", "o", "u", "b", "i", "n", "g2", "r2")
            val bingoItems = BingoItems.create(boardSize = 3, alphabets = alphabets)

            Then("size*size 개의 아이템이 생성된다.") {
                bingoItems.size shouldBe 9
            }

            Then("itemOrder 는 1..9 로 채워진다.") {
                bingoItems.map { it.itemOrder }.sorted() shouldBe (1..9).toList()
            }

            Then("초기 생성 아이템은 title 이 비어 isUpdated 가 아니다.") {
                bingoItems.isAllUpdated() shouldBe false
            }
        }
    }

    Given("findOf") {
        val bingoItems = items()

        When("존재하는 아이템 아이디로 조회하면") {
            Then("해당 아이템을 반환한다.") {
                bingoItems.findOf(2L).id shouldBe 2L
            }
        }

        When("존재하지 않는 아이템 아이디로 조회하면") {
            Then("BingoIllegalStateException 이 발생한다.") {
                shouldThrow<BingoIllegalStateException> {
                    bingoItems.findOf(999L)
                }.message shouldBe "입력된 빙고 아이템 아이디가 잘못 되었습니다. id : 999"
            }
        }
    }

    Given("isAllUpdated") {
        When("모든 아이템에 title 이 있으면") {
            Then("true 를 반환한다.") {
                items(updated = true).isAllUpdated() shouldBe true
            }
        }

        When("title 이 없는 아이템이 하나라도 있으면") {
            Then("false 를 반환한다.") {
                items(updated = false).isAllUpdated() shouldBe false
            }
        }
    }

    Given("sortedByOrder") {
        When("itemOrder 가 뒤섞인 아이템을 정렬하면") {
            val unordered = BingoItems.of(
                listOf(
                    BingoItem.of(3L, "c", null, "g", 3, "#2787C9", mutableSetOf()),
                    BingoItem.of(1L, "a", null, "g", 1, "#2787C9", mutableSetOf()),
                    BingoItem.of(2L, "b", null, "g", 2, "#2787C9", mutableSetOf())
                )
            )

            Then("itemOrder 오름차순으로 정렬된다.") {
                unordered.sortedByOrder().map { it.itemOrder } shouldBe listOf(1, 2, 3)
            }
        }
    }

    Given("completeMembersOf") {
        val bingoItems = items()

        When("일부 아이템만 해당 회원이 완료한 상태에서 조회하면") {
            val memberId = 1L
            bingoItems.findOf(1L).complete(memberId)

            Then("아이템 개수만큼 결과가 나오되 완료하지 않은 칸은 null 이다.") {
                val result = bingoItems.completeMembersOf(memberId)
                result.shouldHaveSize(bingoItems.size)
                result.count { it != null } shouldBe 1
            }
        }
    }

    Given("shuffle") {
        When("아이템을 셔플하면") {
            val bingoItems = items(size = 3)
            bingoItems.shuffle()

            Then("아이템 개수는 유지된다.") {
                bingoItems.size shouldBe 9
            }

            Then("itemOrder 는 1..9 로 빠짐없이 재배치된다.") {
                bingoItems.map { it.itemOrder }.sorted() shouldBe (1..9).toList()
            }
        }
    }
})
