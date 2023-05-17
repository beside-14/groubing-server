package com.beside.groubing.groubingserver.domain.bingo.payload.response

import com.beside.groubing.groubingserver.domain.bingo.domain.map.BingoLine
import com.beside.groubing.groubingserver.domain.bingo.domain.map.BingoMap
import com.beside.groubing.groubingserver.domain.bingo.domain.map.Direction

class BingoMapResponse private constructor(
    val nickName: String,

    val bingoLines: List<BingoLineResponse>,

    val totalCompleteCount: Int,

    val horizontalCompleteLineIndexes: List<Int>,

    val verticalCompleteLineIndexes: List<Int>,

    val diagonalCompleteLineIndexes: List<Int>
){
    class BingoLineResponse private constructor(
        val direction: Direction,

        val bingoItems: List<BingoItemResponse>
    ) {
        companion object {
            fun fromBingoLine(bingoLine: BingoLine, memberId: Long): BingoLineResponse {
                return BingoLineResponse(
                    direction = bingoLine.direction,
                    bingoItems = bingoLine.bingoItems
                        .map { BingoItemResponse.fromBingoItem(it, memberId) }
                )
            }
        }
    }

    companion object {
        fun fromBingoMap(bingoMap: BingoMap, nickName: String): BingoMapResponse {
            return BingoMapResponse(
                nickName = nickName,
                bingoLines = bingoMap.getBingoLines(Direction.HORIZONTAL)
                .map { BingoLineResponse.fromBingoLine(it, bingoMap.memberId) },
                totalCompleteCount = bingoMap.calculateTotalCompleteCount(),
                horizontalCompleteLineIndexes = bingoMap.getCompleteLineIndexes(Direction.HORIZONTAL),
                verticalCompleteLineIndexes = bingoMap.getCompleteLineIndexes(Direction.VERTICAL),
                diagonalCompleteLineIndexes = bingoMap.getCompleteLineIndexes(Direction.DIAGONAL)
            )
        }
    }
}
