package com.beside.groubing.domain.bingo.entity

import com.beside.groubing.domain.bingo.domain.BingoGoal
import com.beside.groubing.domain.bingo.domain.BingoSize
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
