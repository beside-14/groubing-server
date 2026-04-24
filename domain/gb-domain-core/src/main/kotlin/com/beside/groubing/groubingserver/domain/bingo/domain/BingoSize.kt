package com.beside.groubing.groubingserver.domain.bingo.domain

import com.beside.groubing.groubingserver.domain.bingo.exception.BingoInputException

class BingoSize private constructor(
    val size: Int
) {
    fun getMaxGoal(): Int = (size * 2) + 2

    companion object {
        private const val MIN = 3
        private const val MAX = 10
        private val cache = (MIN..MAX).map { BingoSize(it) }

        fun cache(size: Int): BingoSize {
            if (size !in MIN..MAX) {
                throw BingoInputException("빙고 사이즈가 잘못 되었습니다.")
            }
            return cache[size - MIN]
        }
    }
}
