package com.beside.groubing.groubingserver.domain.bingo.domain

import com.beside.groubing.groubingserver.domain.bingo.exception.BingoInputException
import jakarta.persistence.Embeddable

@Embeddable
class BingoGoal private constructor(
    val goal: Int
) {
    companion object {
        fun create(goal: Int, bingoSize: BingoSize): BingoGoal {
            if (goal !in 1..bingoSize.getMaxGoal()) {
                throw BingoInputException(
                    "입력된 Bingo Goal 범위 밖에 있습니다. goal: $goal, bingoSize:${bingoSize.size}, maxBingoGoal:${bingoSize.getMaxGoal()}"
                )
            }
            return BingoGoal(goal)
        }
    }
}
