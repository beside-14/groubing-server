package com.beside.groubing.groubingserver.domain.bingo.domain.map

import com.beside.groubing.groubingserver.domain.bingo.domain.BingoItem

class BingoMap(
    val memberId: Long,
    val bingoSize: Int,
    bingoItems: List<BingoItem>
) {
    val bingoLines: List<BingoLine>

    init {
        bingoLines = mutableListOf<BingoLine>().apply {
            addAll(
                (0 until bingoSize).map { lineIndex ->
                    BingoLine(
                        Direction.HORIZONTAL,
                        lineIndex + 1,
                        bingoItems.slice(lineIndex * bingoSize until (lineIndex + 1) * bingoSize)
                    )
                }
            )
            addAll(
                (0 until bingoSize).map { lineIndex ->
                    BingoLine(
                        Direction.VERTICAL,
                        lineIndex + 1,
                        (0 until bingoSize).map { index ->
                            bingoItems[index * bingoSize + lineIndex]
                        }
                    )
                }
            )
            addAll(
                listOf(
                    BingoLine(
                        Direction.DIAGONAL,
                        1,
                        (0 until bingoSize).map { index -> bingoItems[index * (bingoSize + 1)] }
                    ),
                    BingoLine(
                        Direction.DIAGONAL,
                        2,
                        (0 until bingoSize).map { index -> bingoItems[(index + 1) * (bingoSize - 1)] }
                    )
                )
            )
        }
    }

    fun calculateTotalBingoCount(): Int {
        return bingoLines
            .filter { it.isBingo(memberId) }
            .size
    }

    fun getBingoLines(direction: Direction): List<BingoLine> {
        return bingoLines
            .filter { it.direction == direction }
    }

    fun getBingoIndexes(direction: Direction): List<Int> {
        return getBingoLines(direction)
            .filter { it.isBingo(memberId) }
            .map { it.lineNumber - 1 }
    }
}
