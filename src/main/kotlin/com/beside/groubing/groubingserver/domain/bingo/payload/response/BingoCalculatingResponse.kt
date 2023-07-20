package com.beside.groubing.groubingserver.domain.bingo.payload.response

import com.beside.groubing.groubingserver.domain.bingo.domain.map.BingoMap
import com.beside.groubing.groubingserver.domain.bingo.domain.map.Direction

class BingoCalculatingResponse private constructor(
    val totalBingoCount: Int,

    val horizontalBingoIndexes: List<Int>,

    val verticalBingoIndexes: List<Int>,

    val diagonalBingoIndexes: List<Int>
) {
    companion object {
        fun fromBingoMap(bingoMap: BingoMap): BingoCalculatingResponse {
            return BingoCalculatingResponse(
                totalBingoCount = bingoMap.calculateTotalBingoCount(),
                horizontalBingoIndexes = bingoMap.getBingoIndexes(Direction.HORIZONTAL),
                verticalBingoIndexes = bingoMap.getBingoIndexes(Direction.VERTICAL),
                diagonalBingoIndexes = bingoMap.getBingoIndexes(Direction.DIAGONAL)
            )
        }
    }
}
