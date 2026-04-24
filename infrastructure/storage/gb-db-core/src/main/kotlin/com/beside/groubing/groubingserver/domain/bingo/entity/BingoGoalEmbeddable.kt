package com.beside.groubing.groubingserver.domain.bingo.entity

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoGoal
import com.beside.groubing.groubingserver.domain.bingo.domain.BingoSize
import jakarta.persistence.Embeddable

@Embeddable
class BingoGoalEmbeddable(
    val goal: Int
) {
    fun toDomain(bingoSize: BingoSize): BingoGoal = BingoGoal.create(goal, bingoSize)

    companion object {
        fun from(domain: BingoGoal): BingoGoalEmbeddable = BingoGoalEmbeddable(domain.goal)
    }
}
