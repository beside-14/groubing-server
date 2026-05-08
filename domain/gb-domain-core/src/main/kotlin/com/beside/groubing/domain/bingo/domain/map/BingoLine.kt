package com.beside.groubing.domain.bingo.domain.map

import com.beside.groubing.domain.bingo.domain.BingoItem

class BingoLine(
    val direction: Direction,

    val lineNumber: Int,

    val bingoItems: List<BingoItem>
) {
    fun isBingo(memberId: Long): Boolean {
        return bingoItems.all {
            it.isCompleted(memberId)
        }
    }
}
