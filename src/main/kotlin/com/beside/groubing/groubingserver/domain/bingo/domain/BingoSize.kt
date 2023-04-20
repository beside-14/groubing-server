package com.beside.groubing.groubingserver.domain.bingo.domain

import com.beside.groubing.groubingserver.domain.bingo.exception.BingoInputException
import jakarta.persistence.Embeddable

@Embeddable
class BingoSize private constructor(
    val size: Int
) {
    companion object {
        private const val MIN = 3
        private const val MAX = 10
        private val bingoSizeCache = (MIN..MAX).map { BingoSize(it) }.toList()

        fun cache(size: Int): BingoSize {
            if (size !in MIN..MAX) {
                throw BingoInputException("빙고 사이즈가 잘못 되었습니다.")
            }
            return bingoSizeCache[size - MIN]
        }
    }

    fun getMaxGoal(): Int {
        return (size * 2) + 2
    }
}
