package com.beside.groubing.groubingserver.domain.bingo.domain

import com.beside.groubing.groubingserver.domain.bingo.exception.BingoInputException
import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class BingoGoal private constructor(goal: Int) {
    @Column
    var goal: Int = goal
        private set

    fun edit(goal: Int, bingoSize: BingoSize) {
        isCoverage(goal, bingoSize)
        this.goal = goal
    }

    companion object {
        fun create(goal: Int, bingoSize: BingoSize): BingoGoal {
            isCoverage(goal, bingoSize)
            return BingoGoal(goal)
        }

        private fun isCoverage(goal: Int, bingoSize: BingoSize) {
            if (goal !in 1..bingoSize.getMaxGoal()) {
                throw BingoInputException("입력된 Bingo Goal 범위 밖에 있습니다. goal: $goal, bingoSize:${bingoSize.size}, maxBingoGoal:${bingoSize.getMaxGoal()}")
            }
        }
    }
}
