package com.beside.groubing.domain.bingo.domain

import com.beside.groubing.domain.bingo.domain.map.BingoMap
import com.beside.groubing.domain.bingo.domain.map.Direction
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe

class BingoMapTest : BehaviorSpec({
    val memberId = 1L

    fun createItems(bingoSize: Int): List<BingoItem> =
        (0 until (bingoSize * bingoSize)).map { BingoItem.create(it, imageUrl = "g") }

    fun List<BingoItem>.complete(memberId: Long, vararg indexes: Int) {
        indexes.forEach { this[it].complete(memberId) }
    }

    Given("3x3 빙고맵에서 완료된 아이템이 하나도 없을 때") {
        val items = createItems(3)
        val bingoMap = BingoMap(memberId, 3, items)

        When("총 빙고 개수를 계산하면") {
            val count = bingoMap.calculateTotalBingoCount()

            Then("0개가 반환된다") {
                count shouldBe 0
            }
        }
    }

    Given("3x3 빙고맵에서 가로 한 줄(0,1,2)이 완료되었을 때") {
        val items = createItems(3)
        items.complete(memberId, 0, 1, 2)
        val bingoMap = BingoMap(memberId, 3, items)

        When("총 빙고 개수를 계산하면") {
            val count = bingoMap.calculateTotalBingoCount()

            Then("가로 빙고 1개가 인식된다") {
                count shouldBe 1
            }
        }

        When("가로 방향 빙고 인덱스를 조회하면") {
            val indexes = bingoMap.getBingoIndexes(Direction.HORIZONTAL)

            Then("첫 번째 가로 줄(0번 인덱스)만 반환된다") {
                indexes shouldBe listOf(0)
            }
        }
    }

    Given("3x3 빙고맵에서 세로 한 줄(0,3,6)이 완료되었을 때") {
        val items = createItems(3)
        items.complete(memberId, 0, 3, 6)
        val bingoMap = BingoMap(memberId, 3, items)

        When("총 빙고 개수를 계산하면") {
            val count = bingoMap.calculateTotalBingoCount()

            Then("세로 빙고 1개가 인식된다") {
                count shouldBe 1
            }
        }

        When("세로 방향 빙고 인덱스를 조회하면") {
            val indexes = bingoMap.getBingoIndexes(Direction.VERTICAL)

            Then("첫 번째 세로 줄(0번 인덱스)만 반환된다") {
                indexes shouldBe listOf(0)
            }
        }
    }

    Given("3x3 빙고맵에서 좌상-우하 대각선(0,4,8)이 완료되었을 때") {
        val items = createItems(3)
        items.complete(memberId, 0, 4, 8)
        val bingoMap = BingoMap(memberId, 3, items)

        When("총 빙고 개수를 계산하면") {
            val count = bingoMap.calculateTotalBingoCount()

            Then("대각선 빙고 1개가 인식된다") {
                count shouldBe 1
            }
        }

        When("대각선 방향 빙고 인덱스를 조회하면") {
            val indexes = bingoMap.getBingoIndexes(Direction.DIAGONAL)

            Then("첫 번째 대각선(0번 인덱스)만 반환된다") {
                indexes shouldBe listOf(0)
            }
        }
    }

    Given("3x3 빙고맵에서 우상-좌하 대각선(2,4,6)이 완료되었을 때") {
        val items = createItems(3)
        items.complete(memberId, 2, 4, 6)
        val bingoMap = BingoMap(memberId, 3, items)

        When("대각선 방향 빙고 인덱스를 조회하면") {
            val indexes = bingoMap.getBingoIndexes(Direction.DIAGONAL)

            Then("두 번째 대각선(1번 인덱스)만 반환된다") {
                indexes shouldBe listOf(1)
            }
        }
    }

    Given("3x3 빙고맵에서 가로/세로/두 대각선이 한 점(중앙)에서 교차 완료되었을 때") {
        // 가로 1줄(0,1,2) + 세로 1줄(0,3,6) + 대각선 2줄(0,4,8 / 2,4,6)
        // 0,1,2,3,4,6,8 을 완료하면 4개 라인이 동시에 성립한다
        val items = createItems(3)
        items.complete(memberId, 0, 1, 2, 3, 4, 6, 8)
        val bingoMap = BingoMap(memberId, 3, items)

        When("총 빙고 개수를 계산하면") {
            val count = bingoMap.calculateTotalBingoCount()

            Then("교차된 4개 라인이 인식된다") {
                count shouldBe 4
            }
        }
    }

    Given("3x3 빙고맵의 모든 아이템이 완료되었을 때") {
        val items = createItems(3)
        items.complete(memberId, 0, 1, 2, 3, 4, 5, 6, 7, 8)
        val bingoMap = BingoMap(memberId, 3, items)

        When("총 빙고 개수를 계산하면") {
            val count = bingoMap.calculateTotalBingoCount()

            Then("가로3 + 세로3 + 대각선2 = 8개 라인이 모두 인식된다") {
                count shouldBe 8
            }
        }
    }

    Given("4x4 빙고맵에서 가로 한 줄(4,5,6,7)이 완료되었을 때") {
        val items = createItems(4)
        items.complete(memberId, 4, 5, 6, 7)
        val bingoMap = BingoMap(memberId, 4, items)

        When("총 빙고 개수를 계산하면") {
            val count = bingoMap.calculateTotalBingoCount()

            Then("가로 빙고 1개가 인식된다") {
                count shouldBe 1
            }
        }

        When("가로 방향 빙고 인덱스를 조회하면") {
            val indexes = bingoMap.getBingoIndexes(Direction.HORIZONTAL)

            Then("두 번째 가로 줄(1번 인덱스)만 반환된다") {
                indexes shouldBe listOf(1)
            }
        }
    }

    Given("4x4 빙고맵에서 좌상-우하 대각선(0,5,10,15)이 완료되었을 때") {
        val items = createItems(4)
        items.complete(memberId, 0, 5, 10, 15)
        val bingoMap = BingoMap(memberId, 4, items)

        When("대각선 방향 빙고 인덱스를 조회하면") {
            val indexes = bingoMap.getBingoIndexes(Direction.DIAGONAL)

            Then("첫 번째 대각선(0번 인덱스)만 반환된다") {
                indexes shouldBe listOf(0)
            }
        }
    }

    Given("4x4 빙고맵에서 우상-좌하 대각선(3,6,9,12)이 완료되었을 때") {
        val items = createItems(4)
        items.complete(memberId, 3, 6, 9, 12)
        val bingoMap = BingoMap(memberId, 4, items)

        When("대각선 방향 빙고 인덱스를 조회하면") {
            val indexes = bingoMap.getBingoIndexes(Direction.DIAGONAL)

            Then("두 번째 대각선(1번 인덱스)만 반환된다") {
                indexes shouldBe listOf(1)
            }
        }
    }

    Given("5x5 빙고맵에서 좌상-우하 대각선(0,6,12,18,24)이 완료되었을 때") {
        val items = createItems(5)
        items.complete(memberId, 0, 6, 12, 18, 24)
        val bingoMap = BingoMap(memberId, 5, items)

        When("총 빙고 개수를 계산하면") {
            val count = bingoMap.calculateTotalBingoCount()

            Then("대각선 빙고 1개가 인식된다") {
                count shouldBe 1
            }
        }

        When("대각선 방향 빙고 인덱스를 조회하면") {
            val indexes = bingoMap.getBingoIndexes(Direction.DIAGONAL)

            Then("첫 번째 대각선(0번 인덱스)만 반환된다") {
                indexes shouldBe listOf(0)
            }
        }
    }

    Given("5x5 빙고맵에서 세로 두 줄(0열, 4열)이 완료되었을 때") {
        val items = createItems(5)
        // 0열: 0,5,10,15,20 / 4열: 4,9,14,19,24
        items.complete(memberId, 0, 5, 10, 15, 20, 4, 9, 14, 19, 24)
        val bingoMap = BingoMap(memberId, 5, items)

        When("세로 방향 빙고 인덱스를 조회하면") {
            val indexes = bingoMap.getBingoIndexes(Direction.VERTICAL)

            Then("0번, 4번 인덱스가 반환된다") {
                indexes shouldContainExactlyInAnyOrder listOf(0, 4)
            }
        }
    }

    Given("BingoMap 라인 구성") {
        val items = createItems(3)
        val bingoMap = BingoMap(memberId, 3, items)

        When("방향별 라인을 조회하면") {
            val horizontal = bingoMap.getBingoLines(Direction.HORIZONTAL)
            val vertical = bingoMap.getBingoLines(Direction.VERTICAL)
            val diagonal = bingoMap.getBingoLines(Direction.DIAGONAL)

            Then("가로/세로는 사이즈만큼, 대각선은 2개가 생성된다") {
                horizontal shouldHaveSize 3
                vertical shouldHaveSize 3
                diagonal shouldHaveSize 2
            }
        }
    }

    Given("같은 아이템들에 서로 다른 두 멤버가 완료를 기록했을 때") {
        val otherMemberId = 2L
        val items = createItems(3)
        // memberId 는 가로 첫 줄(0,1,2), otherMemberId 는 세로 첫 줄(0,3,6) 완료
        items.complete(memberId, 0, 1, 2)
        items.complete(otherMemberId, 0, 3, 6)

        When("memberId 기준 빙고맵을 계산하면") {
            val count = BingoMap(memberId, 3, items).calculateTotalBingoCount()

            Then("memberId 가 완료한 가로 1줄만 인식된다") {
                count shouldBe 1
            }
        }

        When("otherMemberId 기준 빙고맵을 계산하면") {
            val count = BingoMap(otherMemberId, 3, items).calculateTotalBingoCount()

            Then("otherMemberId 가 완료한 세로 1줄만 인식된다") {
                count shouldBe 1
            }
        }
    }
})
